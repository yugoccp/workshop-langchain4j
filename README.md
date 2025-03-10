# Requisitos

Olá! Para realizar esse Workshop, você precisará das seguintes ferramentas na sua máquina:

- JDK 21
- LMStudio
- Docker
- Docker Compose

E muita memória RAM, CPU e internet xD!

# Desenvolvimento

Este projeto utiliza o Docker e Docker Compose para desenvolvimento.

Execute o Docker Compose para construir e iniciar os contêineres no seu terminal WSL:

```shell
docker compose up -d
```

O contêiner está configurado para mapear a porta 8080 e o volume workspace/src/ para a porta 8080 e a pasta src/ da máquina host.

Toda alteração que você fizer na pasta src/ refletirá na pasta workspace/src/ do contêiner.

Execute o seguinte comando para iniciar a aplicação dentro do contêiner de desenvolvimento:

```shell
# Este comando lhe dará acesso ao bash do contêiner
docker compose exec -it workshop-app bash

# Dentro do bash do contêiner, execute o seguinte comando:
./mvnw compile quarkus:dev
```

Confirme sua opção para contribuir com as informações de build time para o Quarkus community:
```shell
----------------------------
--- Help improve Quarkus ---
----------------------------
* Learn more: https://quarkus.io/usage/
* Do you agree to contribute anonymous build time data to the Quarkus community? (y/n and enter) 
```

Pronto! Acesse o endereço `http://localhost:8080/` para visualizar a aplicação.

# Instalação

## Instalar JDK 21

Caso você queira apenas executar e ver o código, pode fazer isso com o Docker e Docker Compose, sem a necessidade de instalar o JDK.

Mas caso queira desenvolver numa IDE, precisará instalar o JDK 21 na sua máquina para compilar a aplicação.

Caso não tenha uma distribuição preferida, recomendo instalar a versão do Eclipse Temurin 21-LTS:

https://adoptium.net/temurin/releases/

## Instalar LMStudio
Visite o site e siga as instruções para instalar o LMStudio

https://lmstudio.ai/

### Requisitos mínimos para usar o LMStudio
- Apple Silicon Mac (M1/M2/M3) with macOS 13.6 or newer
- Windows / Linux PC with a processor that supports AVX2 (typically newer PCs)
- 16GB+ of RAM is recommended. For PCs, 6GB+ of VRAM is recommended
- NVIDIA/AMD GPUs supported

Assim que instalar o LMStudio, abra o programa e comece a baixar o modelo **Google's Gemma 2B Instruct** de 2.67GB

![lmstudio_gemma.png](resources%2Flmstudio_gemma.png)

## Utilizando o OpenAI

Para esse Workshop, usaremos a chave "demo" da OpenAI. 

Você pode também usar sua conta da OpenAI para realizar esse Workshop e evoluir usando as últimas versões de modelos como o GPT4 e GPT4o!

Siga as instruções a seguir:

1. Crie uma conta na OpenAI. 
2. Crie uma API Key.
3. Crie uma variável de ambiente no seu sistema chamada `OPENAI_KEY` com o valor da sua API Key.
4. Armazene a sua OPENAI_KEY em algum arquivo de ambiente e NÃO adicione no porjeto!
5. Altere o arquivo AiModelFactory.java para utilizar a sua chave, por exemplo:
```java
public static ChatLanguageModel createOpenAIChatModel() {
    return OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_KEY"))
            .logRequests(true)
            .build();
}
```

![openai_apikey.png](resources%2Fopenai_apikey.png)
