# Advanced Programming Final Project: Composition Viewer GUI
### Daisy Fry-Brumit | Due: Dec 14, 2022

## Overview
For this project, I set out to create a Swing based GUI that enables some user to visualize taxonomic abundance data. From a sample .csv file containing sample IDs as row labels, a header with taxa values, and a table full of numeric abundance values, I am able to return pie charts that represent the composition of selected samples. Initially, I had proposed to include additional capabilities to show bar charts for multi-sample viewing, but decided not to pursue this task in my final edition so I could better focus on creating clean code that operates without error. Instead, I added a capability for saving any chart currently in view. Thus, the full scope of tasks performable by a user are:

- Importing data as a .csv file
- Selecting desired samples for viewing from a panel of buttons labeled with provided sample IDs
- Refreshing chart spaces 
- Saving whatever chat is currently in the chart space as an image

## Contents
The project is split into three scripts. AbundanceSample is a class that represents the incoming data in the form of HashMaps, PieChart is a class that includes a JFreeChart item used by the GUI for visualization, and AbundanceGUI is a class that extends JFrame and executes the user-facing tasks. 

### AbundanceSample
AbundanceSample items contain a sample ID label, a hashmap linking taxa labels to their corresponding abundance values within that sample, and a similar hashmap that hold proportions. In addition, this class contains a method readAbundanceTable() which reads in a full table of AbundanceSample items and saves them all in a list. This function lets me loop through a full table of data one sample at a time for chart generation, JButton creation, etc.. Finally, this class includes getters for sample ID, abundance values, and proportion values as these items are all set to private in the class for protection.

### PieChart
PieChart items contain data (provided by the user) and a JFreeChart item that represents the pie chart itself. These are both set to private, so a getter is provided for each, which the GUI script interacts with to fetch pie chart objects. Pie Chart creation is made possible with the use of package JFreeChart, which needs to be installed separately for the script to run.

### AbundanceGUI
This class, which extends a JFrame object, is the biggest of the scripts. The produced GUI is split into four primary panels set with BorderLayout:
- “north” prompt panel responds to setText commands throughout the code to prompt the user into using the GUI effectively.
- “south” button panel holds three buttons which allow the user to read in a .csv file, refresh the figure panel of contents, and save whatever figure is currently in the figure panel. The refresh and save buttons are only enabled when the figure panel is displaying content.
- “east” sample button panel activates when a file is uploaded and displays a button for every sample in the incoming file. Selecting any of these buttons prompts the figure panel into displaying the pie chart that corresponds to the sample.
- “center” figure panel receives a prompt from the sample button panel, calls the PieChart class, and displays the resulting figure on screen.

### Discussion and reflections
Because the figure creation is quite quick, I was able to run all of the GUI processes on the AWT thread. This made thread safety a pretty simple endeavor and I believe I managed to make things safe simply by narrowing my scope wherever I could, and setting global variables to private where possible, and only utilizing static variables for those objects (like the current sample list) where needed. I limited the possibility of user error in the application itself by including prompts, managing all processes through buttons (minimizing opportunity for unique user input), and disabling buttons when it made no sense to use them. Parsing incoming data was really the primary weak spot for error, so I added a try/catch block during parsing and threw my error message as a JOptionPane dialogue to aid the user in fixing problem input. 

If I were to turn this into a full fledged program that I wanted others to use, I do think I would have moved figure creation off of the AWT thread and included another visualization option. I chose to include more capabilities than needed in the AbundanceSample class because I do actually think I may continue on and use that class for other things in the future. The visuals do run slower on my laptop computer with larger datasets (>100 samples), but my desktop runs this pretty quickly regardless of size. A final (major) limitation that I regret with my application is that when the taxa list gets large, the pie charts make less and less sense to use. In hindsight, I wish I had added a function to only include pie “slices” up to a certain proportion and consolidate very small contributing taxa into an “other” category. 

