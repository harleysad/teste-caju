# Harley Souto Amaro Dalva
### Desafio Técnico - CAJU
## Geral

Para a implementação do [autorizador](https://caju.notion.site/Desafio-T-cnico-para-fazer-em-casa-218d49808fe14a4189c3ca664857de72), 
escolhi utilizar Java Spring como linguagem e framework junto com o banco de dados H2 em memória, apenas como facilitador 
dos testes unitários e do teste em geral.

Requisitos do projeto:

Para compilar o projeto rodando os casos de teste e inicializar a aplicação:

```ssh
mvn clean package
mvn spring-boot:run
```

URL para acessar a documentação OpenAPI:

`http://localhost:8080/swagger-ui/index.html`


Para a criação da tabela e seed para dados de teste, criei os scripts SQL `resources/schema.sql` e `resources/data.sql`,
como seed, são criados 3 usuários com os IDs de 1 até 3 

'GET' 'http://localhost:8080/user/all' 

### Problema L1

O sistema atualiza a tabela de saldo do cliente, bem como salva a transação em outra tabela, esse processo é feito
usando transação no banco de dados garantindo o funcionamento mesmo com concorrência, e melhorando a performance
da aplicação uma vez que não é necessário fazer o `SUM` das transações para determinar o saldo.

> **Observação:** Caso o campo ```totalAmount``` for negativo o sistema vai seguir as mesmas regras para determinar o MCC 
> e adicionar o valor como CRÉDITO.



### Problema L2
- Se o mcc for "5411" ou "5412", o tipo da transação será de FOOD.
- Se o mcc for "5811" ou "5812", o tipo da transação será MEAL.
- Para quaisquer outros valores do mcc, o tipo da transação será  CASH.

### Problema L3
Criei uma lista simples de palavras para procurar no campo `merchant` e tentar determinar o MCC correto,
caso o campo merchant não contenha nenhuma das palavras da lista, o sistema usará as difinições de L2.
```
>  - super     => FOOD
>  - mercado   => FOOD
>  - carrefour => FOOD
>  - ifood     => MEAL // IFOOD ???
>  - rest      => MEAL
>  - burg      => MEAL
```

> **Observação:** Esta lista deveria estar no banco de dados e não no código, mas sem um estudo melhor das palavras
>  foi usado hardcoding mesmo.

## L4. Questão aberta

No caso de bancos relacionais (SQL) a maioria deles garante a atomicidade das transações não permitindo que ocorram
2 ou mais alterações de dados ao mesmo tempo (um dos motivos que dificultam a sua escalabilidade horizontal).

O uso de um campo contendo o saldo atual de cada MCC de cada cliente, ajuda bastante a questão da velocidade mesmo 
em grandes volumes de requisições.

Uma melhoria significativa de performance seria uma procedure que além salvar o lançamento já atualizasse o saldo MCC do 
cliente, evitando o vai e volta banco-aplicação, a cada alteração nos dados.

Se o ainda assim não for o suficiente, podemos colocar um banco de dados como o Redis que vai carregar o saldo MCC 
do cliente na primeira chamada, e a partir desse momento le será responsável por manter e entregar o saldo MCC do cliente.
Um processo para atualizar o banco com esses valores periodicamente e em caso de falha do Redis.