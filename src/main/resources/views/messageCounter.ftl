<#-- @ftlvariable name="messageCounterView" type="com.jayway.messagecounter.infrastructure.resources.MessageCounterView" -->
<html>
    <head>
        <link href="//netdna.bootstrapcdn.com/bootstrap/3.1.0/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body>
        <h1>Current number of messages</h1>
        <table class="table">
            <tr>
                <th>Total</th>
                <th>Number of log messages</th>
                <th>Number of service messages</th>
                <th>Number of game messages</th>
                <th>Number of unknown messages</th>
            </tr>
            <tr>
                <td>${statistics.total?html}</td>
                <td>${statistics.numberOfLogs?html}</td>
                <td>${statistics.numberOfGames?html}</td>
                <td>${statistics.numberOfServices?html}</td>
                <td>${statistics.numberOfUnknown?html}</td>
            </tr>
        </table>

    </body>
</html>