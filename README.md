message-counter-lab
===================

An example of a very simple micro service that counts all messages sent in the lab. It's easily deployed to Heroku and uses AMQP (such as RabbitMQ) to listen for messages.
The topic exchange to send messages to is called `lab`. It registers itself as a service on boot.

Prerequisites
-------------
If you want to run the program locally you need:

1. Java 7
2. Maven 3
3. MongoDB (`brew install mongodb`)
4. RabbitMQ (`brew install rabbitmq`)

Deployment
----------
The project contains Heroku configurations in the files `system.properties` and `Procfile`. However you need to [add two properties](https://toolbelt.heroku.com/) pointing out the AMQP uri:

```bash
heroku config:set AMQP_URL="<amqp_url>"
```

Both URLs must contain username and password, port etc.
