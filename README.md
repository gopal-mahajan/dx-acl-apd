[![Jenkins Build](https://img.shields.io/jenkins/build?jobUrl=https%3A%2F%2Fjenkins.iudx.io%2Fjob%2FACL-APD%2520Server(master)%2F)](https://jenkins.iudx.io/job/ACL-APD%20Server(master)/lastBuild/)
[![Jenkins Tests](https://img.shields.io/jenkins/tests?jobUrl=https%3A%2F%2Fjenkins.iudx.io%2Fjob%2FACL-APD%2520Server(master)%2F)](https://jenkins.iudx.io/job/ACL-APD%20Server(master)/lastBuild/testReport/)
[![Jenkins Coverage](https://img.shields.io/jenkins/coverage/jacoco?jobUrl=https%3A%2F%2Fjenkins.iudx.io%2Fjob%2FACL-APD%2520Server(master)%2F)](https://jenkins.iudx.io/job/ACL-APD%20Server(master)/lastBuild/jacoco/)
[![Jenkins Build](https://img.shields.io/jenkins/build?jobUrl=https%3A%2F%2Fjenkins.iudx.io%2Fjob%2FACL-APD%2520Server(master)%2F&label=integration%20tests)](https://jenkins.iudx.io/job/ACL-APD%20Server(master)/lastBuild/Integration_20Test_20Report/)
[![Jenkins Build](https://img.shields.io/jenkins/build?jobUrl=https%3A%2F%2Fjenkins.iudx.io%2Fjob%2FACL-APD%2520Server(master)%2F&label=security%20tests)](https://jenkins.iudx.io/job/ACL-APD%20Server(master)/lastBuild/zap/)


![IUDX](./docs/iudx.png)

# DX Access Control List (ACL) Access Policy Domain (APD) Server
## Introduction
The Data Exchange (DX) Access Control List (ACL) based Access Policy Domain (APD)
is used for creating, requesting and managing policy. Provider, provider delegates could
allow the consumer, consumer delegates to access their resources by writing a policy against it.
Policies are verified by Data Exchange (DX) Authentication Authorization and Accounting Server (AAA) Server to
allow consumer, consumer delegates to access the resource.

<p align="center">
<img src="./docs/acl-apd-overview.png">
</p>

## Features
The features of the DX ACL APD is as follows: 
- Allows provider, provider delegates to create, fetch, manage policies over their resources
- Allows consumers, consumer delegates to fetch policies, request access for resources 
- Emails are sent asynchronously using Vert.x SMTP Mail Client to the provider, provider delegates for resource access requests
- Integrates with DX AAA Server for token introspection to verify access before serving data to the designated user
- Integrates with AX Auditing server for logging and auditing the access for metering purposes
- Uses Vert.x, Postgres to create scalable, service mesh architecture

# Explanation
## Understanding ACL APD
- The section available [here](./docs/Solution_Architecture.md) explains the components/services used in implementing the ACL-APD server
- To try out the APIs, import the API collection, postman environment files in postman
- Reference : [postman-collection](src/main/resources/IUDX-ACL-APD.postman_collection.json), [postman-environment](src/main/resources/IUDX-ACL-APD.postman_collection.json)

  <video src="https://github.com/user-attachments/assets/3c142dd7-8596-4bd0-8bf3-49adcb3922fa" controls="controls" style="max-width: 730px;">
  </video>




# How To Guide
## Setup and Installation
Setup and Installation guide is available [here](./docs/SETUP-and-Installation.md)

# Tutorial
## Tutorials and Explanations
How to get access token

  <video src="https://github.com/user-attachments/assets/bc8aa7af-71a6-4623-8624-dae3e4964bd5" controls="controls" style="max-width: 730px;">
  </video>

  <video src="https://github.com/user-attachments/assets/abc909da-e470-4ce8-a8a1-0c7c11ccbbe1" controls="controls" style="max-width: 730px;">
  </video>

# Reference
## API Docs
API docs are available [here](https://acl-apd.iudx.org.in/apis)

## FAQ
FAQs are available [here](./docs/FAQ.md)

## Contributing
Please find the information about contributing [here](https://github.com/datakaveri/iudx-acl-apd/blob/main/CONTRIBUTING.md)

## License
[View License](./LICENSE)

