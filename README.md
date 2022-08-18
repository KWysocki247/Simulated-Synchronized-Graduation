# Simulated Synchronized Graduation
 Simulates a graduation where processes must wait/block on each other.

Each character is a process - the children, parents, coordinator, and chariman. They each have roles and have to wait for specific queues/signals from each other. 
The project also shows us the importance of mutual exclusino in the critical sections where only one process should be able to make changes at a time to not ruin the flow of all other processes.
