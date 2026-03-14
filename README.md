# 💰 Controle Financeiro

Aplicativo Android de controle financeiro pessoal desenvolvido como parte da **Formação Android — Desafio 3**.

---

## 📱 Descrição

O **Controle Financeiro** é um app que permite ao usuário registrar, visualizar e analisar suas receitas e despesas com persistência local de dados. O grande objetivo do desafio foi a transição do armazenamento em memória para um banco de dados real no dispositivo, utilizando **Room Database**.

### Funcionalidades implementadas

- Registro de receitas e despesas com categoria, conta, data e observação
- Visualização do saldo atual, total de receitas e total de despesas no Dashboard
- Listagem de transações com filtro por tipo (Todas / Receitas / Despesas)
- Edição e exclusão de transações existentes
- Relatórios com total gasto por categoria e barra de progresso percentual
- Dados iniciais populados automaticamente (categorias e contas padrão)
- Persistência completa de dados com Room Database

---

## 🗂 Estrutura do Projeto

```
com.example.controlefinanceiro
│
├── data/
│   ├── entity/
│   │   ├── Conta.kt              → tabela de contas bancárias
│   │   ├── Categoria.kt          → tabela de categorias (Receita/Despesa)
│   │   └── Transacao.kt          → tabela de transações com ForeignKeys
│   │
│   ├── dao/
│   │   ├── ContaDao.kt           → queries de Conta
│   │   ├── CategoriaDao.kt       → queries de Categoria
│   │   └── TransacaoDao.kt       → queries de Transação (SUM, GROUP BY, LIMIT)
│   │
│   ├── db/
│   │   └── AppDatabase.kt        → configuração do banco, Singleton, Migration
│   │
│   └── repository/
│       └── FinanceiroRepository.kt → camada de acesso a dados, isola o banco da UI
│
├── ui/
│   ├── theme/
│   │   ├── Color.kt              → paleta de cores do app
│   │   ├── Theme.kt              → MaterialTheme com esquema de cores
│   │   └── Type.kt               → tipografia
│   │
│   ├── splash/
│   │   └── SplashScreen.kt       → tela inicial com animação de fade
│   │
│   ├── dashboard/
│   │   ├── DashboardScreen.kt    → tela de resumo financeiro
│   │   └── DashboardViewModel.kt → estado do dashboard com combine de Flows
│   │
│   ├── transacao/
│   │   ├── FormularioScreen.kt   → cadastro e edição de transações
│   │   ├── FormularioViewModel.kt→ validação e persistência do formulário
│   │   ├── TransacoesScreen.kt   → listagem com filtros
│   │   └── TransacoesViewModel.kt→ lógica de filtro por tipo
│   │
│   └── relatorios/
│       ├── RelatoriosScreen.kt   → relatório por categoria com barra de progresso
│       └── RelatoriosViewModel.kt→ agrupamento e cálculo de percentuais
│
├── navigation/
│   └── AppNavigation.kt          → rotas do app com objeto Routes
│
├── util/
│   └── Converters.kt             → TypeConverter Date ↔ Long para o Room
│
├── FinanceiroApplication.kt      → inicialização do banco e dados padrão
└── MainActivity.kt               → NavHost + Bottom Navigation
```

---

## 🧠 Tecnologias utilizadas

| Tecnologia | Versão | Uso |
|---|---|---|
| Kotlin | 2.0.21 | Linguagem principal |
| Jetpack Compose | BOM 2024.09 | Interface declarativa |
| Room Database | 2.6.1 | Banco de dados local |
| KSP | 2.0.21-1.0.28 | Processamento de anotações do Room |
| Navigation Compose | 2.7.7 | Navegação entre telas |
| ViewModel | 2.7.0 | Gerenciamento de estado |
| Kotlin Coroutines | 1.7.3 | Operações assíncronas |
| Kotlin Flow | - | Streams reativos |
| Material 3 | - | Componentes de UI |

---

## ScreenShots



## 🏗 Decisões Técnicas

### 1. Jetpack Compose em vez de XML
A interface foi construída 100% com **Jetpack Compose**, eliminando a necessidade de arquivos XML de layout, `ViewBinding` e `RecyclerView Adapters`. Cada tela é uma função `@Composable`, tornando o código mais conciso e declarativo.

### 2. KSP em vez de KAPT
O projeto utiliza **KSP (Kotlin Symbol Processing)** para processar as anotações do Room, em substituição ao KAPT. O KSP é mais rápido, consome menos memória e é a abordagem recomendada para projetos modernos em Kotlin.

### 3. Arquitetura MVVM com Repository Pattern
O app segue a arquitetura **MVVM (Model-View-ViewModel)** recomendada pelo Google:
- A **UI** (Compose) observa o estado do ViewModel e nunca acessa o banco diretamente
- O **ViewModel** expõe `StateFlow` com o estado da tela e delega operações ao Repository
- O **Repository** centraliza o acesso aos dados e isola os DAOs da camada de UI
- Os **DAOs** declaram as queries SQL que o Room implementa automaticamente

### 4. StateFlow + collectAsState
O estado das telas é gerenciado com `StateFlow`, que sempre possui um valor atual. Na UI, `collectAsState()` converte o `StateFlow` em um `State` do Compose, garantindo recomposição automática sempre que os dados mudarem.

### 5. Flow reativo nos DAOs
Os DAOs retornam `Flow<T>` em vez de valores simples. Isso garante que a UI seja atualizada automaticamente sempre que o banco de dados for modificado, sem necessidade de recarregar manualmente os dados.

### 6. Migration
O banco foi criado na **versão 1** sem o campo `observacao` na tabela de transações. Uma **Migration 1→2** foi implementada para adicionar esse campo via `ALTER TABLE`, simulando um cenário real de evolução do esquema sem perda de dados.

### 7. TypeConverter para Date
O Room não suporta o tipo `java.util.Date` nativamente. A classe `Converters` implementa a conversão `Date ↔ Long` (Unix timestamp em milissegundos), registrada no `AppDatabase` via `@TypeConverters`.

### 8. Singleton no AppDatabase
O banco de dados é instanciado uma única vez usando o padrão **Singleton** com `@Volatile` e bloco `synchronized`, garantindo thread-safety e evitando múltiplas conexões simultâneas.

### 9. Dados iniciais com first()
Ao iniciar o app pela primeira vez, categorias e contas padrão são inseridas automaticamente. A verificação usa `.first()` no Flow para ler o estado atual do banco uma única vez, sem manter uma coleta ativa desnecessária.

### 10. Bottom Navigation condicional
A barra de navegação inferior só é exibida nas telas principais (Dashboard, Transações e Relatórios). Nas telas de Splash e Formulário ela é ocultada, verificando a rota atual via `currentBackStackEntryAsState()`.

---

## 🗃 Estrutura do Banco de Dados

```
┌─────────────┐         ┌──────────────────────┐         ┌──────────────┐
│   contas    │         │      transacoes       │         │  categorias  │
│─────────────│         │──────────────────────│         │──────────────│
│ id (PK)     │◄────────│ contaId (FK)          │────────►│ id (PK)      │
│ nome        │         │ categoriaId (FK)       │         │ nome         │
└─────────────┘         │ id (PK)               │         │ tipo         │
                        │ descricao             │         └──────────────┘
                        │ valor                 │
                        │ tipo                  │
                        │ data                  │
                        │ observacao (nullable) │
                        └──────────────────────┘
```

### Relacionamentos
- **1 Conta → N Transações** (ForeignKey com CASCADE)
- **1 Categoria → N Transações** (ForeignKey com CASCADE)

---

## 🚀 Como executar

1. Clone o repositório
2. Abra no **Android Studio Hedgehog** ou superior
3. Aguarde o Gradle sync
4. Execute em um emulador ou dispositivo com **Android 8.0+ (API 26)**

---

## ✅ Requisitos do Desafio Atendidos

- [x] Room Database com Entities, DAOs e AppDatabase
- [x] TypeConverter para o tipo Date
- [x] CRUD completo (Create, Read, Update, Delete)
- [x] Relacionamentos 1-N entre entidades
- [x] Migration implementada (versão 1 → 2)
- [x] Consultas agregadas (SUM, GROUP BY)
- [x] Banco isolado da camada de UI via Repository
- [x] 5 telas: Splash, Dashboard, Transações, Formulário e Relatórios
- [x] Filtro de transações por tipo
- [x] Relatório por categoria

---

*Desenvolvido por Jessica Cafezeiro como parte da Formação Android — Desafio 3*
