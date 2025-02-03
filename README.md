# 📦 Tracker Logistic API

**Tracker Logistic API** é um sistema de rastreamento de pacotes e eventos de logística, desenvolvido com **Spring Boot
**, **MySQL**, **Redis** e **mensageria assíncrona**.

## 🚀 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **Spring Cache (Redis)**
- **Spring Async**
- **Spring Validation**
- **Spring Scheduler**
- **HikariCP (Gerenciamento de Conexões)**
- **MySQL 8**
- **Log4j2 (Logging)**
- **JUnit 5 e Mockito (Por conta do tempo da entrega e trabalho não foi possivel realziar os testes unitarios da
  aplicação)**

---

## ⚡ **Como Rodar a Aplicação Localmente**

### **1️⃣ Pré-requisitos**

Antes de rodar a aplicação, certifique-se de ter instalado:

- **Java 21**
- **Maven**
- A aplicação ainda não está com o docker-compose, então é necessário ter o **MySQL** instalado na sua máquina.

### **2️⃣ Configuração do Banco de Dados**

A aplicação utiliza **MySQL** como banco de dados.

### **3️⃣ Rodar a Aplicação**

Você pode rodar a aplicação de duas formas:

#### ✅ **Opção 1: Usando Maven**

Para rodar com essa opção é necessário ter o maven instalado na sua maquina.

```bash
mvn spring-boot:run
```

#### ✅ **Opção 2: Usando Java diretamente**

```bash
java -jar target/tracker-logistic.jar
```

---

## 📚 **Endpoints da API**

### 📦 **1. Criar um Pacote**

**`POST /api/parcels`**

```json
{
  "description": "Livros para entrega",
  "sender": "Loja ABC",
  "recipient": "João Silva",
  "estimatedDeliveryDate": "24/10/2025"
}

```

**🔹 Response (201 CREATED)**

```json
{
  "id": "pacote-12345",
  "description": "Livros para entrega",
  "sender": "Loja ABC",
  "recipient": "João Silva",
  "status": "CREATED",
  "createdAt": "2025-01-20T10:00:00Z",
  "updatedAt": "2025-01-20T10:00:00Z"
}

```

---

### 🔄 **2. Atualizar Status do Pacote**

**`PATCH /api/parcels/{parcelId}/status`**

```json
{
  "status": "IN_TRANSIT|CREATED|DELIVERED"
}
```

**🔹 Response (200 OK)**

```json
{
  "id": "pacote-12345",
  "description": "Livros para entrega",
  "sender": "Loja ABC",
  "recipient": "João Silva",
  "status": "CREATED",
  "createdAt": "2025-01-20T10:00:00Z",
  "updatedAt": "2025-01-20T10:00:00Z",
  "deliveredAt": "2025-01-20T10:00:00Z"
  //Preenchido apenas quando for entrengue
}
```

---

### ❌ **3. Cancelar um Pacote**

**`PATCH /api/parcels/{parcelId}/cancel`**
**🔹 Response (200 OK)**

```json
{
  "id": "pacote-12345",
  "status": "CANCELED",
  "updatedAt": "2025-01-30T12:30:00Z"
}
```

---

### 📍 **4. Processamento de Eventos de Rastreamento**

#### **Envio de Evento de Rastreamento**

**`POST /api/events`**

Para cada atualização que ocorrer em um pacote, devemos receber um evento que registra o que aconteceu.

**Request Body:**

```json
{
  "packageId": "pacote-12345",
  "location": "Centro de Distribuição São Paulo",
  "description": "Pacote chegou ao centro de distribuição",
  "date": "2025-01-20T11:00:00Z"
}
```

---

### 🔍 **5. Consultar um Pacote por ID**

**`GET /api/parcels/{parcelId}`**

**Parâmetros opcionais:**

- `showEvents=true` (default) → Retorna os eventos do pacote.
- `showEvents=false` → Exclui os eventos do retorno.

---

### 📜 **6. Consultar Pacotes com Filtros (Paginação)**

**`GET /api/parcels?sender=Loja+ABC&recipient=João+Silva&page=0&size=10`**

**Parâmetros:**

- `sender` → Filtrar por remetente.
- `recipient` → Filtrar por destinatário.
- `page` → Número da página (default: 0).
- `size` → Tamanho da página (default: 10).

---

## 🚀 **Melhorias Futuras**
- Implementação docker/docker-compose para facilitar a execução da aplicação.
- Implementação de **Kafka/RabbitMQ** para eventos de rastreamento assíncronos.
- Melhorias na política de expurgo de dados antigos.
- Implementação de **Spring Security** para autenticação e autorização.
---