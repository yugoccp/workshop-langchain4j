# Instruções de Setup

Olá! Para realizar esse Workshop, você precisará das seguintes ferramentas na sua máquina:

- Java JDK 21
- Quarkus CLI
- LMStudio

E uma boa internet xD!

Dica: Você pode usar o SDKMan para ajudar a instalar algumas ferramentas

https://sdkman.io/

Dica[2]: Se você é usuário Windows, também pode preferir utilizar o chocolatey ;)

https://chocolatey.org/install

## Instalar Java JDK 21
Visite o site e baixe o JDK 21 (lembre-se de configurar a variável ambiente!)

https://adoptium.net/temurin/releases/?package=jdk&arch=any&os=any

Ou utilize SDKMan:
```shell
sdk install java 21.0.2-tem
sdk use java 21.0.2-tem
```

Ou utilize o chocolatey:
```shell
choco install temurin
```

Verifique se a versão correta está instalada e disponível:
```shell
java --version

# Output esperado:
# openjdk 21.0.2 2024-01-16 LTS
# OpenJDK Runtime Environment Temurin-21.0.2+13 (build 21.0.2+13-LTS)
# OpenJDK 64-Bit Server VM Temurin-21.0.2+13 (build 21.0.2+13-LTS, mixed mode)
```

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

## Acessar OpenAI
Você pode também usar sua conta da OpenAI para realizar esse Workshop!
Precisa cadastrar seu cartão de crédito e o custo para realizar o Workshop é mínimo (menos de 1 dólar).
Você verá GPT-3.5 tem um tempo de resposta e sofisticação muito melhor do que os modelos executados localmente.

Para seguir com o Workshop usando a OpenAI, utilize os testes de integração definidos no arquivo WorkshopOpenAiITest.

1. Crie uma conta na OpenAI. 
2. Crie uma API Key.
3. Crie uma variável de ambiente no seu sistema chamada `OPENAI_KEY` com o valor da sua API Key.

![openai_apikey.png](resources%2Fopenai_apikey.png)