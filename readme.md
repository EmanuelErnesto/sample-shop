<h1 align="center">sample-shop</h1>

Este projeto consiste em uma API para uma loja ao qual permite operações como criação de produtos, pedidos e pagamewntos, bem como operações de leitura, atualização, listagem e deleção dos mesmos.

## Pré-requisitos

Antes de começar, verifique se sua máquina possui os seguintes requisitos:

- **Jdk**: versão 17 ou superior. Se você ainda possui a JDK instalada, siga o tutorial de instalação [aqui](https://techexpert.tips/pt-br/windows-pt-br/instalar-java-jdk-no-windows/).

- **Docker**: necessário para executar tanto o Postgres quanto o Pgadmin e o sonarqube localmente. Instruções de instalação estão disponíveis [aqui](https://docs.docker.com/get-docker/).

- **Git**: essencial para clonar o repositório. Baixe-o [aqui](https://www.git-scm.com/downloads).

## Instalação e Configuração

1. **Clone o repositório**:

```bash
https://github.com/EmanuelErnesto/sample-shop
```


2. **Navegue Até a pasta do projeto**

  ```bash
  cd sample-shop

  ```

⚠️ Certifique-se que as portas `8081`, `3307` e `9090` estejam livres para que o docker consiga subir os contêineres corretamente.

<h1>🔧 Executando a API</h1>

Você deverá acessar o docker desktop (caso esteja no windows), após isto acessar o terminal e rodar o comando:

```bash
docker-compose up -d
```

Feito isto, o docker irá subir os contêineres de banco de dados e o prometheus juntamente da aplicação

Para garantir que os contêineres estão funcionando corretamente, rode:

```bash
docker ps
```

Este comando irá listar os contêineres que estão rodando. É esperado que retorne 3 contêineres no terminal.

Para visualizar as rotas e a documentação (swagger) da API, acesse:

http://localhost:8081/api/v1/swagger-ui/index.html#/

Assim, todos os testes irão rodar e você verá em algum tempo no terminal o resultado deles.

<h1>Tecnologias utilizadas</h1>

- Java(17) / Springboot 3.4.2
- MySQL como banco de dados.
- Documentação com swagger.
- Validação dos dados da requisição com Jakarta Bean Validation.
- Contêinerização com docker.
- Testes de integração com Rest Assured/TestContainers e unitários Com JUnit 5/Mockito.
- Consumo de API externa com OpenFeign.
- Observabilidade com Prometheus.