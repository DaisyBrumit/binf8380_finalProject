package binf8380_finalProject;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

public class AbundanceGUI extends JFrame {
	// List of samples available at any one time
	private static List<AbundanceSample> sampleList = new ArrayList<AbundanceSample>();
	// Current chart in use at any given time
	private static JFreeChart pieChart;
	private static final long serialVersionUID = 1L;
	
	// panels
	private JPanel promptPanel = new JPanel();
	private JPanel figurePanel = new JPanel(); // holds figures
	private JPanel bottomPanel = new JPanel(); // holds (most) buttons
	private JPanel samplePanel = new JPanel(); // holds a list of available samples
	
	// buttons and labels
	private JLabel promptLabel = new JLabel(); // instructions (NORTH 'panel')
	private final JButton uploadButton = new JButton("upload .csv"); // bottomPanel
	//private final JButton singleButton = new JButton("View Available Samples"); // bottomPanel
	private final JButton saveButton = new JButton("Save Current Chart"); // bottomPanel
	
	private AbundanceGUI() {
		// basic window
		super("Composition Viewer 1.3");
		setSize(800,800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		getContentPane().setLayout(new BorderLayout());
				
		// initialize prompt label
		promptLabel.setText("Welcome! Upload a .csv file to begin.");
		promptPanel.add(promptLabel);
				
		// bottom panel buttons and such
		bottomPanel.setLayout(new GridLayout(0,3));
		bottomPanel.add(uploadButton);
				
		uploadButton.addActionListener(new uploadListener());
		//singleButton.addActionListener(new singleSampleListener());
		saveButton.addActionListener(new saveListener());
				
		// add panels
		getContentPane().add(promptPanel, BorderLayout.NORTH);
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		getContentPane().add(new JScrollPane(samplePanel), BorderLayout.EAST);
		getContentPane().add(figurePanel, BorderLayout.CENTER);
				
		setVisible(true);
	}
	
	private class uploadListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				// start by emptying existing visuals
				samplePanel.removeAll();
				samplePanel.repaint();
				figurePanel.removeAll();
				figurePanel.repaint();
				
				// try to get data
				FetchData();
				setSamplePanel();
						
				// only add new buttons after FetchData() works
				promptLabel.setText("Select single or multi sample viewing option.");
				//bottomPanel.add(singleButton); // single view
				bottomPanel.add(saveButton);
				saveButton.setEnabled(false); // nothing to save yet!
				
				// remove existing figures
				setVisible(true);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}}
	}
	
	/*
	private class singleSampleListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			setSamplePanel();
			//setVisible(true);
		}
	}
	*/
	
	private class saveListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				saveChart();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	private class pieChartListener implements ActionListener {
		private AbundanceSample sample; // needs to be sample specific
		
		public pieChartListener(AbundanceSample sample) {
			this.sample = sample;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// clear any existing chart(s)
			figurePanel.removeAll();
			
			// call chart object
			PieChart pieObject = new PieChart(sample);
			//ChartPanel piePanel = pieObject.getPie();
			pieChart = pieObject.getPie();
			figurePanel.add(new ChartPanel(pieChart));
			saveButton.setEnabled(true); // save current image
			
			// repaint panel with new chart object
			figurePanel.revalidate();
			figurePanel.repaint();
		}
	}
	
	private void FetchData() throws Exception
	{
		// create the file chooser
		final FileNameExtensionFilter csv_filter = new FileNameExtensionFilter("CSV file", "csv"); // filter only tsv
		JFileChooser chooseFile = new JFileChooser();
		chooseFile.setFileFilter(csv_filter);
		int return_value = chooseFile.showOpenDialog(getParent());
		
		// use selected file as parameter for reading function
		if (return_value == JFileChooser.APPROVE_OPTION) {
			File file = chooseFile.getSelectedFile();
			sampleList = AbundanceSample.readAbundanceTable(file);
		}
		
	}
	
	private void saveChart() throws Exception
	{
		final FileNameExtensionFilter png_filter = new FileNameExtensionFilter("PNG file", "png");
		JFileChooser fileDestChooser = new JFileChooser();
		fileDestChooser.setFileFilter(png_filter);
		int return_value = fileDestChooser.showSaveDialog(null);
		
		if (return_value == JFileChooser.APPROVE_OPTION) {
			File file = fileDestChooser.getSelectedFile();
			ChartPanel outPanel = new ChartPanel(pieChart);
			outPanel.setSize(800,800);
			ChartUtilities.saveChartAsPNG(file, 
			    	pieChart,
			    	outPanel.getWidth(),
			        outPanel.getHeight());
		}
	}
	
	private void setSamplePanel()
	{
		samplePanel.removeAll(); // reset contents
		
		promptLabel.setText("Select sample for viewing from right-hand selection"); // be helpful
		
		samplePanel.setLayout(new GridLayout(sampleList.size()+1,0)); // need dimension to match # of samples + 1 for label

		// add new button for each available sample
		for (AbundanceSample sample : sampleList) {
			JButton tmp_button = new JButton();
			tmp_button.setText(sample.getID().toString()); // label buttons with Sample ID
			tmp_button.addActionListener(new pieChartListener(sample)); // AL for this sample
			samplePanel.add(tmp_button); // add buttons to panel

		samplePanel.revalidate();
		samplePanel.repaint();
		setVisible(true);
		}
	}
	
	
	public static void main(String[] args)
	{
		new AbundanceGUI();
	}
}
