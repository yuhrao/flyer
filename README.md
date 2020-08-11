<h1 align="center">Flyer</h1>
<p align="center">🚀 Uma aplicação para determinar a rota com o melhor preço entre dois aeroportos. 🚀</p>
<p align="center">
  <img alt="Clojure" src="https://img.shields.io/static/v1?label=Clojure&color=27ae60&message=v1.10.1&style=for-the-badge">
  <img alt="Java" src="https://img.shields.io/static/v1?label=Java&color=e74c3c&message=open-jdk-8+&style=for-the-badge">
  <img alt="Java" src="https://img.shields.io/static/v1?label=Flyer&color=3498db&message=v1.0.0&style=for-the-badge">
</p>

## Tabela de conteúdos

* [Instalação](#instalação)
* [Utilização](#utilização)
   * [Executar diretamente](#executar-diretamente)
   * [Executar pelo java](#executar-pelo-java)
* [Testes](#testes)
* [Documentação](#documentação)
   * [Resumo](#resumo)
   * [Observações](#observações)
   * [Iteração via console](#iteração-via-console)
      * [Exemplo](#exemplo)
   * [Iteração via API Rest](#iteração-via-api-rest)
      * [Cadastro de novas escalas](#cadastro-de-novas-escalas)
      * [Cadastro de novas escalas](#cadastro-de-novas-escalas-1)
* [Estrutura de pastas](#estrutura-de-pastas)
   * [Código fonte](#código-fonte)
   * [Testes](#testes-1)
* [Decisões de design](#decisões-de-design)
* [Melhorias](#melhorias)
* [License](#license)


## Instalação

Para executar a aplicação, você pode optar pelas seguintes formas:

- Clonar este repositório `git clone https://github.com/YuhriBernardes/flyer.git`
- Baixar o arquivo compilado na aba de [releases](https://github.com/YuhriBernardes/flyer/releases).

## Utilização

Para executar o programa é necessário um arquivo CSV.
Nesse arquivo, cada linha representa uma rota entre dois aeroportos, onde o primeiro item é a origem da rota, o segundo é o destino e o terceiro item é o custo da viagem.

**OBS: O arquivo CSV não deve conter cabeçalho**

```csv
GRU,BRC,10
BRC,SCL,5
GRU,CDG,75
GRU,SCL,20
GRU,ORL,56
ORL,CDG,5
SCL,ORL,20
```

### Executar diretamente

Tendo o clojure instalado na máquina, rode o comando:

    $ clojure -m flyer.main <arquivo-de-entrada>

### Executar pelo java

Executando o arquivo compilado:

    $ java -jar flyer.jar <arquivo-de-entrada>

Caso não tenha baixado o arquivo compilado ([flyer.jar](https://github.com/YuhriBernardes/flyer/releases)), execute os commandos abaixos.

Gerar Uberjar:

    $ clojure -A:uberjar

## Testes

Rode o comando a seguir:

    $ clojure -A:test -m kaocha.runner

## Documentação

### Resumo
Ao inicializar a aplicação, estarão disponíveis duas interfaces para operação. Uma diretamente pelo console e outra via API REST.

### Observações
1. Por padrão, o servidor WEB utiliza a porta 3000
2. É obrigatório mandar via headder o "Content-Type"

### Iteração via console
Dada a inicialização da aplicação, será exibida uma mensagem solicitando que entre com a origem e destino desejadas para que possa ser calculada a rota de menor preço. A entrada fornecida deve atender ao padrão `<origem>-<destino>` (exemplo: "GRU-CDG").

#### Exemplo
```
Please enter the route: GRU-CDG
Best route: GRU - BRC - SCL - ORL - CDG > $40,00
```

### Iteração via API Rest
Para iteração via API é fornecido um endpoint na rota `/route` com os métodos `GET` e `POST` disponíveis. Com eles é possível obter rotas e cadastrar novas escalas. Documentação da API também disponível via:

- Swagger na rota raiz (`/`).
- [Insomnia Workspace](https://github.com/YuhriBernardes/flyer/blob/main/doc/flyer_insomnia.json) - doc/flyer_insomnia.json (Veja como importar [aqui](https://support.insomnia.rest/article/52-importing-and-exporting-data))

#### Cadastro de novas escalas
**Endpoint:** `/route`
**Método:** `POST`
**Formato:** `application/json`

**Retornos**
- Status 201 = Criado com sucesso

**Corpo da requisição***
```json
{
 "origin": "string",
 "destination": "string",
 "value": 0
}
```
onde `origin` é a origem da escala, `destination` é o destino da escala e `value` é o custo para realizar a escala.

**Exemplo de requisição**
```shell
curl --request POST \
  --url http://localhost:3000/route \
  --header 'content-type: application/json' \
  --data '{"origin": "A",
 "destination": "B",
 "value": 12.2
}'
```

#### Cadastro de novas escalas
**Endpoint:** `/route`
**Método:** `POST`
**Formato de retorno:** `application/json`
**Parâmetros de busca (query):** `origin` = origem | `destination` = destino

**Retornos**
- Status 200 = Sucesso
- Status 404 = Não foi encontrada nenhuma rota

**Corpo do retorno da requisição**
```json
{
 "path": ["string"],
 "value": 0
 }
```
onde `path` é um vetor com a sequência de escalas que deve ser realizada e `value` é o valor total do trajeto.

**Exemplo de requisição**
```shell
curl --request GET \
  --url 'http://localhost:3000/route?origin=GRU&destination=CDG'
```

## Estrutura de pastas

### Código fonte
```text
./flyer/src
└── flyer
    ├── console
    │   ├── io.clj
    │   └── main.clj
    ├── core
    │   ├── dijkstra.clj
    │   ├── file.clj
    │   ├── graph.clj
    │   ├── operations.clj
    │   └── path_finder.clj
    ├── main.clj
    └── server
        ├── common_interceptors.clj
        ├── configuration.clj
        ├── main.clj
        └── routes
            └── route.clj

5 directories, 12 files
```

### Testes

```
./flyer/test
├── flyer
│   ├── console
│   │   └── io_test.clj
│   ├── core
│   │   ├── dijkstra_test.clj
│   │   ├── file_test.clj
│   │   ├── graph_test.clj
│   │   ├── operations_test.clj
│   │   └── path_finder_test.clj
│   └── server
│       ├── common_interceptors_test.clj
│       ├── configuration_test.clj
│       └── routes
│           └── route_test.clj
├── test-resources
│   └── input-sample.txt
└── test_utils
    └── file.clj

7 directories, 11 files
```

## Decisões de design

Dado que a aplicação disponibiliza duas interfaces para utilização (API REST e console), foi optado por isolar a camada que detinha as regras e operações para que esta seja consumida pelas interfaces. Desta forma, todos os processos relacionados às regras de negócio foram alocadas na pasta `core` e para cada interface foi criado um pacote isolado (`server` e `console`).

## Melhorias
1. Traduzir este documento para ingês.
2. Por padrão, considerar os corpos das requisições como `application/json`.
3. Possibilitar a configuração da porta na qual o web server será disponibilizado.
4. Melhorar o isolamento das operaçòes de I/O.
5. Realizar testes funcionais da aplicação (hoje há somente testes unitários).
6. Disponibilizar aplicação via Docker.
  - Imagem para aplicação Web Server
  - Imagem para aplicação console
  - Imagem para aplicação console + Web Server
7. Utilizar database em memória (atualizar o arquivo de entrada via `cron` talvez...)
8. Substituir aplicação de console por uma aplicação web (cljs + reagent).

### Melhorias na aplicação Web Server

- Histórico de pesquisas
- Alterar endpoint de busca de rotas para outro path (`/route/match`)
- Adição de recursos ao endpoint de escalas
  - Obter todas escalas cadastradas
  - Implementar filtro na obtenção das escalas
  - Editar escalas cadastradas

## Licença

Copyright © 2020 Yuhri

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
