# Instruções para o Workshop

## Crie um projeto Quarkus

Faça um fork do projeto do Workshop
```shell
git clone https://github.com/yugoccp/workshop-tdc-summit-sp-2024.git
```

OU crie um projeto novo Quarkus com o seguinte comando
```shell
quarkus create workshop-tdc-summit-2024

cd workshop-tdc-summit-2024
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

Copie o arquivo WorkshopTest.java para o seu diretório de testes `src/test/java/org/acme`.

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

### 2. Prompt Template

Os Prompts são os inputs para os modelos, que contempla o texto a ser completado, contexto e instruções.

Para criar Prompts reutilizáveis, podemos usar os Prompt Templates, uma linguagem de template que nos permite definir um padrão para diferentes inputs. 

Isso ajuda a modificar apenas partes específicas dos prompts conforme necessário.

Vamos criar uma classe chamado `org.acme.bots.EmojiBot.java`.
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

Diferente do que muitos imaginam, os modelos atuais não possuem memória sobre os inputs que enviamos. 

A maioria deles são modelos pré-treinados, com os valores dos pesos da sua rede neural já calculada a partir da grande massa de dados usados durante o seu treinamento.

Mas como o ChatGPT e outros serviços de chat mantém uma conversa coerente? 

Para manter o contexto da conversa, é necessário re-enviar todas as mensagens anteriores a cada novo Prompt que escrevemos para o modelo.

Veremos como isso funciona com o exemplo a seguir. Vamos criar uma classe `org.acme.bots.ResumeBot.java`
- Essa classe deve receber o modelo como parâmetro no construtor.
- Crie uma instância de memória `MessageWindowChatMemory.withMaxMessages(10)`
- Adicione uma SystemMessage na memória para ser uma instrução fixa do NumberBot:
```java
SystemMessage.from("""
    You are an AI skilled in analyzing user-provided information to craft professional resumes. 
    When a user provides you with descriptions of their skills or any other relevant details, acknowledge each entry with a simple 'Ok.' 
    Once the user requests a summary, synthesize all the acknowledged information into a coherent 
    and concise resume summary that highlights the user's qualifications and strengths.
    """)
```
- Implemente um método chamado `chat(String message)` que retorna o output do modelo como String.
- Adicione cada mensagem do usuário no chatMemory `chatMemory.add(UserMessage.from(message))`
- Envie todas as mensagens para o chatModel `chatMemory.messages()`
- Adicione a resposta do modelo na memória também para ser enviado na próxima interação `chatMemory.add(aiMessage)`

No WorkshopTest, descomente o teste `test_3_Memory()` e execute o comando de teste.

### 4. Retrieval Augmented Generation (RAG)

Os modelos não podem usar informações que não foram incluídas no seu treinamento. Além disso, eles têm um limite no tamanho do texto que conseguem analisar de uma vez, e processar textos grandes demora mais tempo.

Então, como lidar com muitas informações ao mesmo tempo, como analisar um livro ou artigo longo? Uma solução é utilizar a arquitetura RAG (Retrieval Augmented Generation), que foi criada para ajudar nesse problema.

Vamos supor que precisamos analisar uma página de notícias e queremos um resumo dos assuntos de tecnologia:
1. Transformamos toda a página em vetores usando um Modelo de Embeddings.
2. Convertemos também o pedido do usuário ("resuma os assuntos de tecnologia da página de notícia") em vetor. Assim, conseguimos identificar as partes da notícia que se relacionam com o pedido.
3. Com base nessas partes, criamos um novo pedido para o modelo: "Resuma os assuntos de tecnologia da página de notícia, com base nas informações selecionadas: {{informações selecionadas}}"
4. Isso nos permite focar apenas nas informações relevantes ao pedido do usuário, tornando o Prompt final menor e o processo mais eficiente.

Para esse exercício, precisaremos adicionar uma nova dependência para o Embedding Model:
```xml
<dependency>
   <groupId>dev.langchain4j</groupId>
   <artifactId>langchain4j-embeddings-all-minilm-l6-v2</artifactId>
   <version>0.28.0</version>
</dependency>
```

- Crie uma classe com o nome `org.acme.bots.DocumentBot`
- Essa classe deve receber ChatLanguageModel, EmbeddingModel, EmbeddingStore, e uma String com o arquivo a ser carregado como parâmetros no construtor.
- Crie uma instância de memória `MessageWindowChatMemory.withMaxMessages(10)`
- Adicione uma SystemMessage na memória para ser uma instrução fixa do NumberBot:


### 5. Tools (Function Calling)

Nem todos os modelos suportam Tools. Atualmente os seguintes modelos podem ser usados:
- OpenAiChatModel
- AzureOpenAiChatModel
- LocalAiChatModel
- QianfanChatModel

## Seu Gerenciador de Prompts pessoal!
