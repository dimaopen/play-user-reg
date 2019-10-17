# Description
A Sample Play Framework project written in Scala. 
It is supposed to register farmers and register their property around the world.

It gives some understanding how to create authentication mechanism in Play. 
See controllers.actions.AuthenticatedAction for source code. It forbid doing an operation for unauthenticated users.
Or it allows to do certain operations to only particular users. In a similar way concept of Roles can be implemented.

# Libraries used
Play Framework, Slick (for working with a DB), Guice (for DI)  