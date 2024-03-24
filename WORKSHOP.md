# Instruções para o Workshop

## Crie um projeto Quarkus

Faça um fork do projeto do Workshop e clone na sua máquina
```shell
git clone https://github.com/yugoccp/workshop-tdc-summit-sp-2024.git
```

Execute a aplicação em modo de desenvolvedor para validar que tudo ocorreu bem:
```shell
./mvnw compile quarkus:dev
```

## Adicione as dependências do Langchain4J
Adicione as dependências do [Langchain4J](https://docs.langchain4j.dev/get-started) no seu pom.xml
```xml
<properties>
   ...
    <langchain4j.version>0.28.0</langchain4j.version>
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

Atualmente, existem muitos modelos de linguagem grande (LLM) disponíveis, que se diferenciam em tamanho, desempenho e finalidade. 

É possível executar esses modelos localmente usando ferramentas como LMStudio, Ollama, GPT4All, LocalAI, ou por meio de serviços de SaaS oferecidos por empresas como OpenAI, Anthropic, Google Gemini e MistralAI.

Da mesma forma que o código aberto é compartilhado no GitHub, esses modelos podem ser encontrados e acessados em plataformas como o [HuggingFace](https://huggingface.co/). 

O LMStudio, por exemplo, utiliza o HuggingFace para disponibilizar os modelos. 

Vamos criar uma classe Java chamado `org.acme.factories.AiModelFactory.java`
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

Os Prompts são os inputs para os modelos, que contempla o texto a ser completado, contexto e instruções.

Para criar Prompts reutilizáveis, podemos usar os Prompt Templates, uma linguagem de template que nos permite definir um padrão para diferentes inputs. 

Isso ajuda a modificar apenas partes específicas dos prompts conforme necessário.

Abra a classe chamada `org.acme.bots.DebuggerAssistant.java`.
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

Diferente do que muitos imaginam, os modelos atuais não possuem memória sobre os inputs que enviamos. 

A maioria deles são modelos pré-treinados, com os valores dos pesos da sua rede neural já calculada a partir da grande massa de dados usados durante o seu treinamento.

Mas como o ChatGPT e outros serviços de chat mantém uma conversa coerente? 

Para manter o contexto da conversa, é necessário re-enviar todas as mensagens anteriores a cada novo Prompt que escrevemos para o modelo.

Veremos como isso funciona com o exemplo a seguir. Vamos implementar a classe `org.acme.bots.ChatAssistant.java`
- Instancie o `chatMemory` no construtor da classe com uma memória de 10 mensagens: `MessageWindowChatMemory.withMaxMessages(10)`
- Implemente o método `chat()`
  - Adicione cada mensagem do usuário no chatMemory `chatMemory.add(UserMessage.from(message))`
  - Gere uma resposta com o chatModel enviando todas as mensagens `chatMemory.messages()`
  - Adicione a resposta do modelo na memória também para ser enviado na próxima interação `chatMemory.add(aiMessage)`
- Retorne a  resposta do modelo

Valide a implementação utilizando o teste `test_3_Memory()`.

### 4. Retrieval Augmented Generation (RAG)

Para utilizar contextos muito grandes, como um livro, ou uma base de dados mais volumosa, não podemos simplesmente enviar tudo no prompt do modelo.

Vimos que os modelos têm um limite no tamanho do input que conseguem analisar de uma vez, e processar inputs grandes demora mais tempo.

Então o que podemos fazer? 

Uma solução é utilizar o RAG, ou Retrieval-Augmented Generation, uma técnica que combina a recuperação de informações com a geração de texto. 

Basicamente, o RAG se divide em duas etapas:
1. Organizar o contexto, de forma que eu possa recuperar o que é relevante, dado um input
2. Ao receber um prompt dp usuário, buscar apenas os trechos relevantes do contexto para adicionar no prompt, antes de enviar para o modelo processar.

Essencialmente, o RAG seleciona e fornece somente as informações relevantes do contexto para o input do usuário. 
Assim, o modelo recebe apenas o necessário para processar o input, evitando um processamento pesado ou exceder o limite de tokens do modelo.

Para organizar o contexto, precisaremos usar outro tipo de modelo, chamado Embedding Models. 

Os Embedding Models são modelos de NLP (Natural Language Processing) que transformam texto em vetores, para que possamos encontrar sentenças relacionadas.

Para esse exercício, precisaremos adicionar uma nova dependência no projeto para usar o Embedding Model:
```xml
<dependency>
   <groupId>dev.langchain4j</groupId>
   <artifactId>langchain4j-embeddings-all-minilm-l6-v2</artifactId>
   <version>0.28.0</version>
</dependency>
```
- No construtor da classe `DocumentAssistant.java`, instancie o documentAssistant com o método `buildDocumentAssistant()`
- Implemente o método `chat()` apenas chamando o `documentAssistant.chat()`.

No teste `test_4_RAG()` utilizamos o arquivo resources/hckrnews.html (notícias do Hacker News) como contexto para realizarmos perguntas ao modelo.

Execute o teste e avalie o resultado. 

Por simplicidade, mantive a construção de algumas etapas para esse exercício. 
Aproveite para explorar a implementação das classes utilitárias para melhor compreensão do mecanismos do RAG.

### 5. Tools (Function Calling)

Os LLMs são úteis para analisar dados e sugerir ações. Contudo, eles não podem realizar ações no mundo externo diretamente. 

Para contornar essa limitação, foi desenvolvido o conceito de "Tools" ou "Function Calls". 

Isso permite que os LLMs indiquem a necessidade de realizar uma ação específica, como uma busca na internet, sem fazer a ação por si mesmos. 

O modelo simplesmente sugere que uma ferramenta externa deveria ser usada. 

Cabe aos desenvolvedores implementar e executar essas ações externas com base nas instruções do LLM, retornando os resultados ao modelo para continuação do processo.

O Langchain4J já implmenta esse fluxo, mas nem todos os modelos suportam Tools. Atualmente os seguintes modelos podem ser usados:
- OpenAiChatModel
- AzureOpenAiChatModel
- LocalAiChatModel
- QianfanChatModel

O `SearchAssistant.java` possibilita que o modelo faça buscas no Google através da ferramenta `SearchTools.searchGoogle`. 

Quando o modelo determina a necessidade de uma pesquisa no Google, ele indica isso na resposta. 

A pesquisa é então realizada, e o resultado é incorporado ao contexto para as próximas interações. 

Para ver esse processo em ação, execute o teste `test_5_Tools()`.

## Construa Seu Gerenciador de Assistentes pessoal!

MyAssistants é um gerenciador de assistentes para os seus modelos.

Esta aplicação foi desenvolvida para armazenar seus Prompts mais usados e simplificar o uso de modelos para resolver seus problemas do dia-a-dia!

A aplicação possui duas telas:
1. PromptView: Lista de prompts que você pode selecionar para iniciar uma conversa com os modelos.
2. ChatView: Painel com Chat para conversar com o modelo usando um dos prompts que possui armazenado.

