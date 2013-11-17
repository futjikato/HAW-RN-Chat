# Dokumentation for HAW-RN-Chat

Declarations:

```
->				starts/calls/throws
<-				catches an task
<->				interacts with (method call)
something		a class
[something]		user action
(something)		a task
// something    description of a process
OK:				will be performed if previous action succeeded
ER:				will be performed if previous action failed
```

## table of content

* [Client start](#start)
* [User enters server address](#askServer)
* [TCP server connection failed](#CONNECT_FAILED)
* [TCP server connection established](#CONNECT_SUCCESS)
* [User enters username](#askUsername)
* [Username assignment failed](CUSER_REQUESTNAMECHANGEFAILED)
* [Username assignment succeeeded](#CUSER_NAMECHANGE)
* [Refresh of client list is requested](#FETCHUSER_REQUEST)
* [Process a client list](#FETCHUSER_PROCESS)
* [User enters a chat message](#userChatMessage)
* [User receives a message](#remoteChatMessage)
* [Quit application](#QUIT)
* [Detailed Task process](#HowToTask)

### <a name="start"></a> Client start

All needed threads will be started.<br>
The GuiManager also triggers a user question for the server address.

```
main
	-> ChatRemoteManager
	-> GuiManager
		-> GuiApplication
		-> [askServer]
	-> IntervalManager
	-> P2PManager
		-> UdpServer
	-> Manager
```

### <a name="askServer"></a> User enters server address [askServer]

```
[askServer]
	-> (CONNECT_REQUEST)
		<- ChatRemoteManager
			// establishes server connection
			<-> ServerConnection
			OK: -> (CONNECT_SUCCESS)
			ER: -> (CONNECT_FAILED)
```

### <a name="CONNECT_FAILED"></a> In case connection failed

```
(CONNECT_FAILED)
	<- GuiManager
		-> [inform user]
		-> [askServer]
```

### <a name="CONNECT_SUCCESS"></a> In case connection could be established

```
(CONNECT_SUCCESS)
	<- GuiManager
		-> [inform user]
		-> [askUsername]
```

### <a name="askUsername"></a> User enters username

```
[askUsername]
	-> (CUSER_REQUESTNAMECHANGE)
		<- ChatRemoteManager
			// send NEW command to server
			<-> ServerConnection
			OK: -> (CUSER_NAMECHANGE)
			ER: -> (CUSER_REQUESTNAMECHANGEFAILED)
```

### <a name="CUSER_REQUESTNAMECHANGEFAILED"></a> In case username was invalid/already in use

```
(CUSER_REQUESTNAMECHANGEFAILED)
	<- GuiManager
		-> [inform user]
		-> [askUsername]
```

### <a name="CUSER_NAMECHANGE"></a> In case username could be assigned

```
(CUSER_NAMECHANGE)
	<- IntervalManager
		-> (FETCHUSER_REQUEST)
		-> IntervalTrigger
			// in an interval of 5 seconds
			-> (FETCHUSER_REQUEST)
	<- GuiManager
		-> [inform user]
```

### <a name="FETCHUSER_REQUEST"></a> Refresh of client list is requested

```
(FETCHUSER_REQUEST)
	<- ChatRemoteManager
		// loads list from TCP server
		<-> ServerConnection
		// parses response
		<-> InfoParser
		-> (FETCHUSER_PROCESS)
```

### <a name="FETCHUSER_PROCESS"></a> Process a client list

```
(FETCHUSER_PROCESS)
	<- P2PManager
		// clears current clients and adds new inet addresses
		<-> ClientManager
```

### <a name="userChatMessage"></a> User enters chat message

```
[userChatMessage]
	-> (CUSER_NEWMESSAGE)
		<- P2PManager
			<-> ClientManager
				<-> UdpSender
```

### <a name="remoteChatMessage"></a> User receives a message

```
[remoteChatMessage]
	-> UdpServer
		-> (RUSER_NEWMESSAGE)
			<- GuiManager
				-> [show to user]
```

### <a name="QUIT"></a> Quit application

All running threads are stoped.

```
[quit]
	-> (QUIT)
		<- ChatRemoteManager
		<- GuiManager
			<-> GuiApplication
		<- P2PManager
			<-> UdpServer
		<- IntervalManager
			<-> IntervalTrigger
		<- Manager
```

### <a name="HowToTask"></a> How a Task is processed

A Taks kann be published anywhere in the code.<br>
You get the singleton instance of the Manager and call publishTask. The task will be stored in a BlockingQueue.<br>
All threads that can process a task extend from the TaskWoker class. This abstract class implements the Observer interface while the Manager class is observable. All TaskWoker are threads. If a TaskWoker thread starts it will start to observe the Manager.<br>
The Manager-Thread ( main ) will pick up the Tasks will then notify up all tasks within the queue and notify all observers and thus all TaskWorkers.<br>
By adding ActionListener on an TaskWoker the woker "listens" on that event.