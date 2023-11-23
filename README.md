# Safety - Fraud Detection System

Safety é um projeto que visa detectar transações fraudulentas em um sistema financeiro. Ele utiliza autenticação JWT para garantir a segurança das transações e roles admin e user para diferenciar as permissões de diferentes tipos de usuários.

## Funcionalidades Principais

1. **Autenticação Segura:** O sistema utiliza autenticação JWT para garantir a segurança das transações. Cada usuário recebe um token JWT após o login bem-sucedido, que deve ser incluído em cada solicitação subsequente para autenticação com duração de até 10 minutos.

2. **Controle de Acesso com Roles:** Existem duas roles principais no sistema - admin e user. A role admin tem permissões mais amplas, incluindo a capacidade de visualizar relatórios de transações e modificar configurações do sistema, enquanto a role user tem permissões limitadas para realizar transações.

3. **Bloqueio após Tentativas Malsucedidas:** O sistema bloqueia automaticamente o acesso após três tentativas malsucedidas de login. Isso ajuda a prevenir tentativas de login por força bruta.

4. **Limites de Transação Dinâmicos:** O sistema define limites específicos para transações com base no histórico do usuário. Para a primeira transação, o limite é de até 1000.00. Para transações subsequentes, é calculado um valor médio com base nas transações anteriores. O limite para transações futuras é definido como 1,5 vezes o valor médio da transação.

5. **Detecção de Fraude:** Antes de processar uma transação, o sistema verifica se o valor da transação excede o limite de detecção de fraude. Isso é calculado como 1,5 vezes o valor médio da transação. Se a transação for considerada potencialmente fraudulenta, ela será rejeitada. Além de verificar se a mesma transação com valor e usuário foi feito igual no período de 10 minutos, se isso ocorrer as próximas transações é negada, apenas aceitando a primeira.

## Instruções de Configuração e Execução

### Requisitos

- Java 17
- Spring Boot 3.1.1
- Spring Security 6
- Maven
- NoSQL (MongoDB)

### Configuração

1. Configure as propriedades do banco de dados no arquivo `application.yml`.
2. Necessário inserir as variáveis de ambiente, como por exemplo: `profile=test;my-secret-key=my-secret-key`. 
2. Execute o comando para instalar as dependências: `mvn clean install`.
3. Inicie o aplicativo com o comando: `mvn spring-boot:run`.

### Uso

1. Faça login utilizando as credenciais fornecidas.
2. Após o login, acesse as funcionalidades disponíveis com base na sua role (admin ou user).
3. Ao realizar transações, o sistema verificará automaticamente se a transação é potencialmente fraudulenta com base nos limites estabelecidos.

## Estrutura do Projeto

O projeto é organizado em diferentes pacotes para facilitar a manutenção e escalabilidade. As principais classes e pacotes incluem:

- `controllers`: Controladores que lidam com as solicitações HTTP.
- `domain`: Classes e DTOs que são armazenadas no banco não relacional.
- `exceptions`: Exception Handler para customizar algumas exceções, bem como definir de forma que não repita código.
- `services`: Lógica de negócios e serviços relacionados à detecção de fraude.
- `repositories`: Acesso ao banco de dados e interação com as entidades.
- `infra.security`: Configuração de segurança, incluindo a implementação de autenticação JWT e controle de acesso com base em roles.

## Contribuições

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues, propor novas funcionalidades ou enviar pull requests para melhorar este sistema de detecção de fraude.

## Contato

Para mais informações ou dúvidas, entre em contato com [rayanabonfanti@gmail.com].
