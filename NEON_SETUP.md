# Configuração do Neon

1. No painel do Neon, abra **Connect** e copie os dados da conexão PostgreSQL.
2. A URL usada pelo Spring deve começar com `jdbc:postgresql://`.
3. Configure as variáveis de ambiente:

```text
DATABASE_URL=jdbc:postgresql://SEU_HOST.neon.tech/SEU_BANCO?sslmode=require
DATABASE_USERNAME=SEU_USUARIO
DATABASE_PASSWORD=SUA_SENHA
```

No PowerShell:

```powershell
$env:DATABASE_URL="jdbc:postgresql://SEU_HOST.neon.tech/SEU_BANCO?sslmode=require"
$env:DATABASE_USERNAME="SEU_USUARIO"
$env:DATABASE_PASSWORD="SUA_SENHA"
.\mvnw.cmd spring-boot:run
```

Com `spring.jpa.hibernate.ddl-auto=update`, o Hibernate cria ou atualiza as tabelas a partir das classes `@Entity`. Isso não transfere automaticamente os registros existentes do MySQL.
