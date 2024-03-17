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
    <version>0.27.1</version>
</dependency>
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai</artifactId>
    <version>0.27.1</version>
</dependency>
```

## Inicie o servidor LMStudio!

Abra o LMStudio.
1. Na barra lateral, selecione opção **Local Server**.
2. No topo, selecione o modelo **gemma it 2B**
3. Na lateral esquerda, clique no botão verde **Start Server**
   ![lmstudio_setup.png](resources%2Flmstudio_setup.png)

O LMStudio vai disponibilizar as mesmas APIs da OpenAI para o modelo selecionado na URL: `http://localhost:1234/v1`.

## Prática dos Fundamentos

Copie o arquivo WorkshopTest.java para o seu diretório de testes `src/test/java/org/acme`.

### Modelos

Atualmente, existem muitos modelos de linguagem grande (LLM) disponíveis, que se diferenciam em tamanho, desempenho e finalidade. 
Da mesma forma que o código aberto é compartilhado no GitHub, esses modelos podem ser encontrados e acessados através da plataforma HuggingFace. 
O LMStudio, por exemplo, utiliza essa plataforma para buscar modelos. 
É possível executar esses modelos localmente usando ferramentas como LMStudio, Ollama, GPT4All, LocalAI, ou por meio de serviços de SaaS oferecidos por empresas como OpenAI, Anthropic, Google Gemini e MistralAI.

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

No WorkshopTest, vamos descomentar o primeiro teste `testModel()` e validar se estamos conectando corretamente ao modelo:

```java
import org.acme.ChatModelFactory;

public class WorkshopTest {

    @Test
    void testModel() {
        var chatModel = ChatModelFactory.createModel();

        var result = chatModel.generate("hello");

        System.out.println(result);
    }
}
```

Você pode rodar o teste pela sua IDE ou pela linha de comando com o Quarkus:
```shell
quarkus test
```

Funcionando?! :) Então vamos seguir em frente!

### Prompt template


### Memória

### Retrieval Augmented Generation (RAG)

### Agents

### Seu gerenciador de Prompts pessoal!