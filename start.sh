docker build -t java21-runtime .

docker run -it -v ./src:/workspace/src --net=host --env-file .env java21-runtime /bin/bash

