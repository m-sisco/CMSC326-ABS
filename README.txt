Marissa Sisco, Rachel Culpepper, and Tanner Bina

Adding Disease Simulation to Sugarscape
---------------------------------------

Our project added disease simulation to sugarscape. To run our project, simply compile and run the Simulation Manager. All parameters for our project are stored in the Parameters class. You can edit any of the static variables to alter the simulation. 

The core design features of our project is the addition of a disease and immune system class. These were both designed as bit strings as recommended by Growing Artificial Society. We also allow diseases to mutate to create realistic variation in the disese present in society over time. The key design decision about mutation was to have the mutation transfer to the new host while the old host retains the original disease. The immune system works by matching diseases to substrings. The closer the match, the closer the immune system is to being immune to the disease. Our other major design decision was how a disease effects agents. Our diseases simply increase the metabolism of a host by a uniform amount. Although not quite realistic, this implementation allows for us to focus on the spread of disease through the population rather than focus on how diseases themselves effect the population. Diseases are spread when an agent moves: each time an agent moves, it is infected with a random disease from one of its neighbors. We initially had this going the other way, so that an agent infected other agents when it moved, but we ended up changing it to make the events simpler (scheduling update events and changing metabolic rates for the other agents ended up causing issues from editing the event calendar). We decided to make updating the immune system its own event, because we were using the idea from the book of updating a disease every unit time, but we decided these would best be done as separate events, so that each disease is updated every unit of time since the agent was infected, instead of all diseases being updated at the same time regardless of when they were introducedd. All implementation and design decisions are discussed much more in depth in our writeup.

Compiling and running
---------------------

The code needs to be compiled with the squint library.
It is run by calling the main method in SimulationManager
or by creating a new instance of SimulationManager.
