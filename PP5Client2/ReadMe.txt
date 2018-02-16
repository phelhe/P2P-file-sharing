PP5 Write-up Henry Phelps

Testing it on my computer, all parts work. 

How to run it:
The ports are hard coded so just run myBroker in PP5Server, and then myClient in PP5Client, PP5Client1, PP5Client2.

Rating system:
When a user joins they get a rating of 2/5 so they have a chance to be picked over someone with a really bad rating. Then the rating after that is calculated based on the responses of peers downloading their file. 

PP5Client:
   myClient:
      Uses:
     	 -clientReadThread to read input from the user
     	 -ClientUtilities contains utility methods to request files, register files, etc…
         -AppMessage class used as an object to send back and forth between peers and broker
  	 -PeerInfo class used to hold information about a peer such as IP and name
         -PeerServeThread used to accept incoming connections from peers who want a file you have
         -ServeThread spawned off by PeerServeThread to deal with an individual peer and send them the file

   Important Details for testing:
      ClientUtilities constructor — IP and port of the broker must be specified here (hardcoded)
      myClient — has hardcoded port to dangle for distributing files (line 13)
	This port must be different for each peer if you are testing on just one computer

PP5Server:
   myBroker:
      Uses:
        -BrokerUtilities to store data about peers, respond to requests, ratings, registering, unregistering, etc.
	-PeerInfo to hold information for a specific peer
	-AppMessage to send messages to peers
	-BrokerThread spawned off to deal with a connecting client — acts based on the message field of the AppMessage received
   
   Important Details for testing:
      port for the broker to dangle is specified on line 14 (hardcoded)

