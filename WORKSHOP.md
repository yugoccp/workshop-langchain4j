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
```

## Inicie o servidor LMStudio!

Abra o LMStudio.
1. Na barra lateral, selecione opção **Local Server**.
2. No topo, selecione o modelo **gemma it 2B**
3. Na lateral esquerda, clique no botão verde **Start Server**
   ![lmstudio_setup.png](resources%2Flmstudio_setup.png)

O LMStudio vai disponibilizar as mesmas APIs da OpenAI para o modelo selecionado na URL: `http://localhost:1234/v1`.

## Fundamentos

Copie o arquivo WorkshopITest.java para o seu diretório de testes `src/test/java/org/acme`.

### 1. Modelos

Atualmente, existem muitos modelos de linguagem grande (LLM) disponíveis, que se diferenciam em tamanho, desempenho e finalidade. 

É possível executar esses modelos localmente usando ferramentas como LMStudio, Ollama, GPT4All, LocalAI, ou por meio de serviços de SaaS oferecidos por empresas como OpenAI, Anthropic, Google Gemini e MistralAI.

Da mesma forma que o código aberto é compartilhado no GitHub, esses modelos podem ser encontrados e acessados em plataformas como o [HuggingFace](https://huggingface.co/). 

O LMStudio, por exemplo, utiliza o HuggingFace para disponibilizar os modelos. 

Vamos criar uma classe Java chamado `org.acme.factories.AiModelFactory.java`
- Essa classe vai ter um método estático `createChatModel()` que retorna um ChatLanguageModel (do Langchain4J).
- Para criar o modelo, vamos conectar com o LMStudio utilizando o Langchain4J para ajudar :)
- Vamos usar o builder do `OpenAiChatModel` e passar a URL do LMStudio
- A `apiKey` nesse caso pode ser ignorado mas ainda deve ser definido um valor para o builder.
ß
```java
OpenAiChatModel.builder()
    .baseUrl("http://localhost:1234/v1")
    .apiKey("ignore")
    .build();
```

Se você optou por usar o serviço da OpenAI, basta alterar o código com o exemplo abaixo:
```java
OpenAiChatModel.withApiKey(System.getenv("OPENAI_KEY"));
```

No WorkshopITest, descomente o primeiro teste `test_1_Model()`.
```java
class WorkshopITest {
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

### 2. Prompt Template

Os Prompts são os inputs para os modelos, que contempla o texto a ser completado, contexto e instruções.

Para criar Prompts reutilizáveis, podemos usar os Prompt Templates, uma linguagem de template que nos permite definir um padrão para diferentes inputs. 

Isso ajuda a modificar apenas partes específicas dos prompts conforme necessário.

Vamos criar uma classe chamado `org.acme.bots.DebuggerAssistant.java`.
- Essa classe deve receber o modelo como parâmetro no construtor.
- Crie um método chamado `generate(String movieName)` que recebe um nome de um filme como parâmetro e retorna uma String com o output do modelo.
- Utilize o Prompt Template abaixo para descrever as instruções para o modelo.
```java
var emojiTemplate = PromptTemplate.from("""
   From the movie '{{movieName}}', generate a short plot only using emojis
   that illustrates remarkable objects or moments of the movie.
""");
```

Substitua o valor do movieName com o código abaixo
```java
emojiTemplate.apply(Map.of("movieName", movieName));
```

No WorkshopITest, descomente o teste `test_2_PromptTemplate()` e execute o comando de teste.
```java
class WorkshopITest {
   @Test
   void test_2_PromptTemplate() {
       // ...
   }
}
```

Aproveite para ver resultados de diferentes filmes e avaliar a "destreza" do modelo =)

### 3. Memória

Diferente do que muitos imaginam, os modelos atuais não possuem memória sobre os inputs que enviamos. 

A maioria deles são modelos pré-treinados, com os valores dos pesos da sua rede neural já calculada a partir da grande massa de dados usados durante o seu treinamento.

Mas como o ChatGPT e outros serviços de chat mantém uma conversa coerente? 

Para manter o contexto da conversa, é necessário re-enviar todas as mensagens anteriores a cada novo Prompt que escrevemos para o modelo.

Veremos como isso funciona com o exemplo a seguir. Vamos implementar a classe `org.acme.bots.ChatAssistant.java`
- Instancie o `chatMemory` no construtor da classe, com uma memória de 10 mensagens: `MessageWindowChatMemory.withMaxMessages(10)`
- Adicione uma SystemMessage na memória para ser uma instrução fixa do SummaryBot:
```java
SystemMessage.from("""
    You are an AI skilled in analyzing user-provided information to generate coherent and concise summary. 
    When a user provides you with descriptions, acknowledge with an 'Ok.' 
    Once the user requests a summary, synthesize all the acknowledged information into a coherent and concise description.
    """)
```
- No método `chat()`, adicione cada mensagem do usuário no chatMemory `chatMemory.add(UserMessage.from(message))`
- Envie todas as mensagens para o chatModel: `chatMemory.messages()`
- Adicione a resposta do modelo na memória também para ser enviado na próxima interação `chatMemory.add(aiMessage)`

No WorkshopITest, descomente o teste `test_3_Memory()` e execute o comando de teste.

### 4. Retrieval Augmented Generation (RAG)

Os modelos não podem usar informações que não foram incluídas no seu treinamento. Além disso, eles têm um limite no tamanho do input que conseguem analisar de uma vez, e processar inputs grandes demora mais tempo.

E quando precisamos lidar com muitas informações ao mesmo tempo? Como analisar um livro ou um artigo longo? O que podemos fazer? 

Uma solução é utilizar o RAG, ou Retrieval-Augmented Generation, uma técnica de inteligência artificial que combina a recuperação de informações com a geração de texto. 

Funciona da seguinte forma:
- Primeiro, transformamos a fonte de dados em vetores (embeddings) com a ajuda de um modelo de vetores, e armazenamos o resultado.
- Segundo, ao receber um prompt de usuário, o sistema busca da base de vetores as informações relevantes ao que foi solicitado.
- Em seguida, usa essas informações recuperadas como contexto adicional para gerar uma resposta mais precisa e informativa. 

Essencialmente, o RAG seleciona e fornece somente as informações relevantes do contexto para o input do usuário. 
Assim, o modelo recebe apenas o necessário para processar o input, evitando um processamento pesado ou exceder o limite de tokens do modelo.

Para esse exercício, precisaremos adicionar uma nova dependência no projeto para o Embedding Model:
```xml
<dependency>
   <groupId>dev.langchain4j</groupId>
   <artifactId>langchain4j-embeddings-all-minilm-l6-v2</artifactId>
   <version>0.28.0</version>
</dependency>
```

- Crie uma classe com o nome `org.acme.factories.EmbeddingFactory`
- Por simplicidade, trabalharemos com instâncias em memória. Adicione os métodos abaixo para essa classe:
```java
public static EmbeddingModel createEmbeddingModel() {
     return new AllMiniLmL6V2EmbeddingModel();
 }

 public static EmbeddingStore createEmbeddingStore() {
     return new InMemoryEmbeddingStore();
 }
```
- Implemente o método `chat(String filename, String message)` da classe `DocumentBot.java`
```java 
public String chat(String filename, String message) {
        // Transform single file content into chunks of text segments.
        var segments = createTextSegments(filename);

        // Transform segments into embeddings (vectors)
        var embeddings = createEmbeddings(segments);

        // Store embeddings with the corresponding segments
        storeEmbeddings(embeddings, segments);

        // Build RAG assistant which filters context from the document to add to user prompt
        var documentAssistant = buildDocumentAssistant(chatModel, embeddingModel, embeddingStore);

        return documentAssistant.answer(message);
  }
```
No WorkshopITest, descomente o teste `test_4_RAG()` e execute o comando de teste.

### 5. Tools (Function Calling)

Assim como o LLM por padrão não tem acesso à conhecimento externos, eles também não tem a capacidade de executar ações externas.

Para resolver esse problema, existe um conceito chamado "tools" ou "function calling" que permitem que o LLM possa chamar um mais funções disponíveis e definidos pelo desenvolvedor.

Isso permite que os LLMs sinalizem a intenção de usar uma ferramenta, como uma pesquisa na web ou uma API externa, em sua resposta. 

Os LLMs não utilizam a ferramenta diretamente; em vez disso, indicam que querem usá-la. Então, os desenvolvedores devem executar a ferramenta com os argumentos fornecidos pelo LLM e inserir os resultados no sistema.

Nem todos os modelos suportam Tools. Atualmente os seguintes modelos podem ser usados:
- OpenAiChatModel
- AzureOpenAiChatModel
- LocalAiChatModel
- QianfanChatModel

Aqui veremos apenas o funcionamento do `SearchBot.java`.

Perceba que o modelo não realiza pesquisas no Google, mas se disponibilizarmos uma ferramenta, o modelo pode sugerir que precisa executar uma busca em sua resposta.
Assim podemos re-enviar o prompt adicionando o resultado da pesquisa no contexto.

O Langchain4J já disponibiliza uma implementação padrão para esse fluxo.

No WorkshopITest, descomente o teste `test_5_Tools()` e execute o comando de teste.

## Seu Gerenciador de Prompts pessoal!
