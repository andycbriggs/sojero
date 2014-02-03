# sojero

Service Oriented Messaging Platform built on top of JeroMQ

## Background

This project is part of my final year project for a BSc Computer Science.
I plan to use GitHub issues as an aid to project management with Mile stones marking sprints and issues which represent
issues (in agile terms).
Alongside this I will also have an issue label to keep track of the project Diary, a requirement of the project.
By keeping a diary of each commit I can reference what code I was working on that day and will be using GitHub Gist's
for keeping track of Unit tests.

## Message Structure

### Service Discovery
Current uses Java object serialization, plan to use ZMsg across the board eventually.

### Service Messages
ZMsg with the following framing:

|       1       |        2       |      3     |   4    |
| :-----------: |:--------------:| :---------:|:------:|
|   Service ID  |  Message Type  | Message ID |  Data  |

Message Type indicates the handler type Command, Event or Request/Reply
Message ID indicates the specific handler
