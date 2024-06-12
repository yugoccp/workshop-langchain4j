# Instruções para o Workshop

## Valide o ambiente executando a aplicação

Execute a aplicação em modo de desenvolvedor para validar que tudo ocorreu bem:
```shell
./mvnw quarkus:dev
```

## Utilizaremos o Langchain4J para orquestrar nossa interação com os modelos LLM
As dependências do [Langchain4J](https://docs.langchain4j.dev/get-started) foram adicionadas no pom.xml
```xml
<properties>
   ...
    <langchain4j.version>0.31.0</langchain4j.version>
</properties>
...
<dependencies>
    ...
   <dependency>
       <groupId>dev.langchain4j</groupId>
       <artifactId>langchain4j</artifactId>
       <version>${langchain4j.version}</version>
   </dependency>
   <dependency>
       <groupId>dev.langchain4j</groupId>
       <artifactId>langchain4j-open-ai</artifactId>
       <version>${langchain4j.version}</version>
   </dependency>
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-embeddings-all-minilm-l6-v2</artifactId>
        <version>${langchain4j.version}</version>
    </dependency>
</dependencies>
```

## Inicie o servidor LMStudio!

Abra o LMStudio.
1. Na barra lateral, selecione opção **Local Server**.
2. No topo, selecione o modelo **gemma it 2B**
3. Na lateral esquerda, clique no botão verde **Start Server**
   ![lmstudio_setup.png](resources%2Flmstudio_setup.png)

O LMStudio vai disponibilizar as mesmas APIs da OpenAI para o modelo selecionado na URL: `http://localhost:1234/v1`.

## Fundamentos

### 1. Modelos

Abra a classe `AiModelFactory.java`
- Essa classe vai ter dois métodos estáticos `createLocalChatModel()` e `createOpenAiChatModel()`
- Vamos usar o builder do `OpenAiChatModel` para criar os modelos.
- A `apiKey` nesse caso pode ser ignorado mas ainda deve ser definido um valor para o builder.

Para conectar com modelos locais (LMStudio), implemente createLocalChatModel com o seguinte código:
```java
OpenAiChatModel.builder()
       .baseUrl("http://localhost:1234/v1")
       .apiKey("ignore")
       .logRequests(true)
       .timeout(Duration.ofSeconds(300))
       .build();
```

Para conectar com modelos da OpenAI, implemente createOpenAiChatModel com o seguinte código:
```java
OpenAiChatModel.builder()
        .apiKey(System.getenv("OPENAI_KEY"))
        .logRequests(true)
        .build();
```

Para validar se estamos conectando corretamente ao modelo, execute o primeiro teste `test_1_Model()` nos testes WorkshopLocalITest e WorkshopOpenAiITest.
```java
@Test
void test_1_Model() {
   // ...
}
```

Funcionando?! :) Então vamos seguir!

### 2. Prompt Template

Abra a classe chamada `org.acme.assistants.DebuggerAssistant.java`.
- Implemente o método `generate(String movieName)` com o Prompt Template abaixo para descrever as instruções para o modelo.
```java
var emojiTemplate = PromptTemplate.from("""
   Can you identify any bugs in this Java code snippet? 
   {{codeSnippet}}
""");
```

Substitua o valor do codeSnippet com o código abaixo
```java
emojiTemplate.apply(Map.of("codeSnippet", codeSnippet));
```

Finalmente, valide a implementação executando o teste `test_2_PromptTemplate()`.

Aproveite para ver resultados de diferentes códigos e avaliar a "destreza" do modelo =)

### 3. Memória

Vamos implementar a classe `org.acme.assistants.ChatAssistant.java`
- Instancie o `chatMemory` no construtor da classe com uma memória de 10 mensagens: `MessageWindowChatMemory.withMaxMessages(10)`
- Implemente o método `chat()`
  - Adicione cada mensagem do usuário no chatMemory `chatMemory.add(UserMessage.from(message))`
  - Gere uma resposta com o chatModel enviando todas as mensagens `chatMemory.messages()`
  - Adicione a resposta do modelo na memória também para ser enviado na próxima interação `chatMemory.add(aiMessage)`
- Retorne a  resposta do modelo

Valide a implementação utilizando o teste `test_3_Memory()`.

### 4. Retrieval Augmented Generation (RAG)

Para esse exercício, precisaremos do Embedding Model, por isso foi adicionado a dependência `langchain4j-embeddings-all-minilm-l6-v2`
- No construtor da classe `DocumentAssistant.java`, instancie o documentAssistant com o método `buildDocumentAssistant()`
- Implemente o método `chat()` apenas chamando o `documentAssistant.chat()`.

No teste `test_4_RAG()` utilizamos o arquivo resources/hckrnews.html (notícias do Hacker News) como contexto para realizarmos perguntas ao modelo.

Execute o teste e avalie o resultado. 

Por simplicidade, mantive a construção de algumas etapas para esse exercício. 
Aproveite para explorar a implementação das classes utilitárias para melhor compreensão do mecanismos do RAG.

### 5. Tools (Function Calling)

O `SearchAssistant.java` possibilita que o modelo faça buscas no Google através da ferramenta `SearchTools.searchGoogle`. 

Quando o modelo determina a necessidade de uma pesquisa no Google, ele indica isso na resposta. 

A pesquisa é então realizada, e o resultado é incorporado ao contexto para as próximas interações. 

Para ver esse processo em ação, execute o teste `test_5_Tools()`.

## Construa Seu Gerenciador de Prompts pessoal!

MyPrompts é um gerenciador de assistentes para os seus modelos.

Esta aplicação foi desenvolvida para armazenar seus Prompts mais usados e simplificar o uso de modelos para resolver seus problemas do dia-a-dia!

A aplicação possui duas telas:
1. PromptView: Lista de prompts que você pode selecionar para iniciar uma conversa com os modelos.
2. ChatView: Painel com Chat para conversar com o modelo usando um dos prompts que possui armazenado.

A aplicação utiliza o ChatAssistant que implementamos. Adicione as implementações abaixo para tornar a aplicação funcional: 

Implemente um novo construtor para receber o chatModel e uma String como mensagem de sistema. Adicione a mensagem de sistema no chatMemory.
```java
public ChatAssistant(ChatLanguageModel chatModel, String contextMessage) {
    this.chatModel = chatModel;
    chatMemory = MessageWindowChatMemory.withMaxMessages(10);
    chatMemory.add(SystemMessage.from(contextMessage));
}
```

Implemente também o método getMessages() para retornar todas as mensagens da memória. 
```java
public List<ChatMessage> getMessages(){
    return chatMemory.messages();
}
```