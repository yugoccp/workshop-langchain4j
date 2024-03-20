# Instruções para o Workshop

## Crie um projeto Quarkus

Faça um fork do projeto do Workshop ou crie um projeto novo Quarkus com o seguinte comando
```shell
quarkus create workshop-java-llm

cd workshop-java-llm
```

Você pode executar a aplicação em modo de desenvolvedor:
```shell
./mvnw compile quarkus:dev
```

## Adicione as dependências do Langchain4J
Adicione as dependências do [Langchain4J](https://docs.langchain4j.dev/get-started) no seu pom.xml
```xml
<!-- Add dependency at your pom.xml -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j</artifactId>
    <version>0.28.0</version>
</dependency>
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai</artifactId>
    <version>0.28.0</version>
</dependency>
<dependency>
   <groupId>dev.langchain4j</groupId>
   <artifactId>langchain4j-embeddings-all-minilm-l6-v2</artifactId>
   <version>0.28.0</version>
</dependency>
```

## Inicie o servidor LMStudio!

Abra o LMStudio.
1. Na barra lateral, selecione opção **Local Server**.
2. No topo, selecione o modelo **gemma it 2B**
3. Na lateral esquerda, clique no botão verde **Start Server**
   ![lmstudio_setup.png](resources%2Flmstudio_setup.png)

O LMStudio vai disponibilizar as mesmas APIs da OpenAI para o modelo selecionado na URL: `http://localhost:1234/v1`.

## Fundamentos

Copie o arquivo WorkshopTest.java para o seu diretório de testes `src/test/java/org/acme`.

### 1. Modelos

Atualmente, existem muitos modelos de linguagem grande (LLM) disponíveis, que se diferenciam em tamanho, desempenho e finalidade. 

É possível executar esses modelos localmente usando ferramentas como LMStudio, Ollama, GPT4All, LocalAI, ou por meio de serviços de SaaS oferecidos por empresas como OpenAI, Anthropic, Google Gemini e MistralAI.

Da mesma forma que o código aberto é compartilhado no GitHub, esses modelos podem ser encontrados e acessados em plataformas como o [HuggingFace](https://huggingface.co/). 

O LMStudio, por exemplo, utiliza o HuggingFace para disponibilizar os modelos. 

Vamos criar uma classe Java chamado ChatModelFactory.java
- Essa classe vai ter um método estático `createModel()` que retorna um ChatLanguageModel (do Langchain4J).
- Para criar o modelo, vamos conectar com o LMStudio utilizando o Langchain4J para ajudar :)
- Vamos usar o builder do `OpenAiChatModel` e passar a URL do LMStudio
- A `apiKey` nesse caso pode ser ignorado mas ainda deve ser definido um valor para o builder.

```java
OpenAiChatModel.builder()
    .baseUrl("http://localhost:1234/v1")
    .apiKey("ignore")
    .build();
```

Se você quer usar o serviço da OpenAI, implemente o exemplo abaixo:
```java
OpenAiChatModel.withApiKey(System.getenv("OPENAI_KEY"));
```

No WorkshopTest, descomente o primeiro teste `test_1_Model()`.
```java
class WorkshopTest {
   @Test
   void test_1_Model() {
      // ...
   }
}
```

Para validar se estamos conectando corretamente ao modelo, você pode executar o teste pela sua IDE ou com o comando Quarkus:
```shell
quarkus test
```

Funcionando?! :) Então vamos seguir!

### 2. Prompt template

Os Prompts são os inputs para os modelos, que contempla o texto a ser completado, contexto e instruções.

Para criar Prompts reutilizáveis, podemos usar os Prompt Templates, uma linguagem de template que nos permite definir um padrão para diferentes inputs. 

Isso ajuda a modificar apenas partes específicas dos prompts conforme necessário.

Vamos criar uma classe chamado `EmojiBot.java`.
- Essa classe recebe um modelo como parâmetro no construtor.
- Crie um método chamado `generate(String movieName)` que recebe um nome de um filme como parâmetro.
- Utilize o Prompt Template abaixo para descrever as instruções para o modelo.
```java
var emojiTemplate = PromptTemplate.from("""
   From the movie '{{movieName}}':
   1. Generate a summary of the movie plot.
   2. Identify the remarkable objects and moments.
   3. Translate the plot to emojis using the identified objects and moments.
""");
```

Substitua o valor do movieName com o código abaixo
```java
emojiTemplate.apply(Map.of("movieName", movieName));
```

No WorkshopTest, descomente o teste `test_2_PromptTemplate()` e execute o comando de teste.
```java
class WorkshopTest {
   @Test
   void test_2_PromptTemplate() {
       // ...
   }
}
```

Aproveite para ver resultados de diferentes filmes e avaliar a "destreza" do modelo =)

### 3. Memória

Diferente do que muitos imaginam, os modelo LLM atuais não possuem memória sobre os inputs que enviamos. 

A maioria deles são modelos pré-treinados, com os valores dos pesos da sua rede neural já calculada.

Mas como o ChatGPT e outros serviços de chat mantém uma conversa coerente então? 

Para manter o contexto da conversa, é necessário re-enviar todas as mensagens anteriores a cada novo Prompt que escrevemos para o modelo.

Vamos ver como isso funciona com o exemplo a seguir. Vamos criar uma classe `NumberBot.java`
- Essa classe recebe um modelo como parâmetro no construtor.
- Crie uma instância de memória `MessageWindowChatMemory.withMaxMessages(10)`
- Adicione uma SystemMessage na memória para ser uma instrução fixa:
```java
SystemMessage.from("""
    You only accept a valid number or 'Result' as input.
    For invalid input you will reply with 'Invalid input'
    For valid numbers, you reply with 'Ok'
    For 'Result', you will tell me the biggest number between all the numbers I gave to you.
    """)
```
- Implemente um método chamado `chat(String message)` que retorna o resultado do modelo como String:
```java
// Adiciona mensagem do usuário no chatMemory
chatMemory.add(UserMessage.from(message));

// Envia todas as mensagens para o modelo
var aiMessage = chatModel
       .generate(chatMemory.messages())
       .content();

// Adiciona a resposta do modelo na memória para ser enviado na próxima interação
chatMemory.add(aiMessage);
```

No WorkshopTest, descomente o teste `test_3_Memory()` e execute o comando de teste.

### 4. Retrieval Augmented Generation (RAG)

Sabemos que os modelos não possuem dados ou informações que não estiveram presentes durante o seu treinamento.

Aprendemos também que os modelos possuem limites de tokens para o seu input. E quanto maior o input, maior a carga de processamento necessário.

Como podemos trabalhar!1 com um grande volume de dados, mais recentes, ou privados, que não estiveram presentes durante o treinamento?

O RAG (Retrieval Augmented Generation) é uma arquitetura que endereça exatamente essa necessidade.

Podemos entender o RAG nas seguintes etapas:
1. Vetorizar os dados externos com um modelo de embedding
2. Armazenar os dados externos vetorizados em um embedding (vector) storage
3. Para cada interação com o usuário, vetorizar o Prompt com o mesmo modelo de embedding
4. Buscar os dados relevantes no embedding storage a partir do Prompt vetorizado
5. Montar um Prompt final para o modelo juntando o Prompt original + os dados relevantes para contexto

### 5. Tools (Function Calling)

Nem todos os modelos suportam Tools. Atualmente os seguintes modelos podem ser usados:
- OpenAiChatModel
- AzureOpenAiChatModel
- LocalAiChatModel
- QianfanChatModel

## Seu Gerenciador de Prompts pessoal!
