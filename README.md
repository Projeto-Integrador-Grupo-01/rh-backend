# ConectaRH - Backend

## 1. Descrição

O ConectaRH é um projeto integrador voltado à transformação humana e organizacional. Ele une tecnologia, empatia e gestão de pessoas para promover relações de trabalho mais humanas, produtivas e sustentáveis. A proposta é oferecer uma consultoria moderna e acessível em Recursos Humanos, com foco no desenvolvimento de talentos, fortalecimento de equipes e aprimoramento da cultura organizacional.

---

## 2. Sobre esta API

A API foi construída em Java com Spring Boot e oferece funcionalidades completas para o gerenciamento de colaboradores, departamentos e usuários. Ela permite operações de CRUD, autenticação, associação entre entidades e visualização de dados.

### 2.1. Principais Funcionalidades

1. Cadastro e gerenciamento de colaboradores
2. Associação de colaboradores a departamentos
3. Cadastro e gerenciamento de departamentos
4. Autenticação de usuários do sistema
5. Consulta por filtros (nome, cargo, departamento)
6. Cálculo de idade com base na data de nascimento
7. Upload e visualização de fotos de perfil
8. Integração com banco de dados MySQL


---

## 3. Diagrama de Classes

```text
classDiagram
    class Colaborador {
        - Long id
        - String nome
        - String email
        - LocalDate dtNasc
        - String cargo
        - BigDecimal salario
        - String foto
        - Departamento departamento
    }

    class Departamento {
        - Long id
        - String nome
        - String icone
        + List~Colaborador~ colaboradores
    }

    class Usuario {
        - Long id
        - String nome
        - String email
        - String senha
    }

    Colaborador --> Departamento : pertence a
    Departamento --> Colaborador : possui
```

---

## 4. Diagrama Entidade-Relacionamento (DER)

```text
erDiagram
    tb_colaborador {
        bigint id PK
        varchar nome
        varchar email
        date dt_nasc
        varchar cargo
        decimal salario
        varchar foto
        bigint departamento_id FK
    }

    tb_departamento {
        bigint id PK
        varchar nome
        varchar icone
    }

    tb_usuario {
        bigint id PK
        varchar nome
        varchar email
        varchar senha
    }

    tb_colaborador ||--o{ tb_departamento : "pertence a"
```

---

## 5. Tecnologias utilizadas

| Item                          | Descrição  |
| ----------------------------- | ---------- |
| **Servidor**                  | Tomcat embutido    |
| **Linguagem de programação**  | Java |
| **Framework**                 | Spring Boot    |
| **ORM**                       | JPA, Hibernate    |
| **Banco de dados Relacional** | MySQL      |
| **IDE** | Eclipse / STS      |
| **Testes de API** | Insomnia      |
| **Gerenciador de Dependências** | Maven      |
| **Versionamento** | Git + GitHub      |
| **Documentação** | Typora / SharePoint      |

---

## 6. Configuração e Execução

```bash
1. Clone o repositório:git clone https://github.com/seu-usuario/conectarh.git
```

2. Abra o projeto no Eclipse ou STS
3. Configure o banco de dados no arquivo application.properties
4. Execute a aplicação com o Spring Boot
5. Teste os endpoints utilizando o Insomnia ou Postman

---

## 🙌 Equipe

Desenvolvido por [Tech Sisters](https://projeto-integrador-grupo-01.github.io/techsisters/) 🍃



<table align="center">
  <tr>
    <td align="center">
      <a href="https://github.com/LemesdeMorais">
        <img src="https://github.com/LemesdeMorais.png?size=100" width="100" style="border-radius:50%; border:2px solid #00C853;" alt="Rafaela Morais"/>
        <br/><sub><b>Rafaela Morais</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/alineromanini">
        <img src="https://github.com/alineromanini.png?size=100" width="100" style="border-radius:50%; border:2px solid #00C853;" alt="Aline Romanini"/>
        <br/><sub><b>Aline Romanini</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/cdouradom">
        <img src="https://github.com/cdouradom.png?size=100" width="100" style="border-radius:50%; border:2px solid #00C853;" alt="Cinthia Dourado"/>
        <br/><sub><b>Cinthia Dourado</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/nicollyjesus">
        <img src="https://github.com/nicollyjesus.png?size=100" width="100" style="border-radius:50%; border:2px solid #00C853;" alt="Nicolly Jesus"/>
        <br/><sub><b>Nicolly Jesus</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/queren-alves">
        <img src="https://github.com/queren-alves.png?size=100" width="100" style="border-radius:50%; border:2px solid #00C853;" alt="Quéren Alves"/>
        <br/><sub><b>Quéren Alves</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/crissmcoelho">
        <img src="https://github.com/crissmcoelho.png?size=100" width="100" style="border-radius:50%; border:2px solid #00C853;" alt="Cristina Coelho"/>
        <br/><sub><b>Cristina Coelho</b></sub>
      </a>
    </td>
  </tr>
</table>