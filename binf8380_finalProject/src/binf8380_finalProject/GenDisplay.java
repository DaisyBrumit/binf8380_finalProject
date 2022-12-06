package binf8380_finalProject;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.util.SortOrder;


public class GenDisplay extends JFrame
{
	/* 
 * SET GLOBAL VARIABLES
 * CREATE THE CONSTRUCTOR
 * CREATE FUNCIONS TO POPULATE PANELS
 */
	private static final long serialVersionUID = 1L;
	//private final BigDecimal CUTOFF = new BigDecimal("0.01");
	private List<AbundanceSample> sampleList = new ArrayList<AbundanceSample>();
	private JLabel promptLabel = new JLabel();
	
	private JPanel bottomPanel = new JPanel();
	private final JButton uploadButton = new JButton("upload .csv");
	private final JButton singleButton = new JButton("Single Sample View");
	private final JButton multiButton = new JButton("Multi Sample View");
	
	private JPanel samplePanel = new JPanel();
	//private JScrollPane samplePane = new JScrollPane();
	
	private JPanel figurePanel = new JPanel();
	
	public GenDisplay(String title)
	{
		// basic window
		super(title);
		setSize(800,800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		getContentPane().setLayout(new BorderLayout());
		
		// initialize prompt label
		JPanel promptPanel = new JPanel();
		promptLabel.setText("Welcome! Upload a .csv file to begin.");
		promptPanel.add(promptLabel);
		getContentPane().add(promptPanel, BorderLayout.NORTH);
		
		// bottom panel buttons and such
		bottomPanel.setLayout(new GridLayout(0,3));
		bottomPanel.add(uploadButton);
		
		uploadButton.addActionListener(new uploadListener());
		singleButton.addActionListener(new singleSampleListener());
		multiButton.addActionListener(new multiSampleListener());
		
		// add panels
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		getContentPane().add(samplePanel, BorderLayout.EAST);
		getContentPane().add(figurePanel, BorderLayout.CENTER);
		
		setVisible(true);
	}	
	
	private void setSamplePanel(boolean single)
	{
		samplePanel.removeAll();
		JLabel sampleLabel = new JLabel("Sample ID's\n");
		
		samplePanel.setLayout(new GridLayout(sampleList.size()+1,0)); // need dimension to match # of samples + 1 for label
		samplePanel.add(sampleLabel); // add descriptive title
		
		if (single) {
		// add new button for each available sample
			for (AbundanceSample sample : sampleList) {
				JButton tmp_button = new JButton();
				tmp_button.setText(sample.getID().toString()); // label buttons with Sample ID
				tmp_button.addActionListener(new pieChartListener(sample)); // AL for this sample
				samplePanel.add(tmp_button); // add buttons to panel
			}
		}
		
		else {
		// same process but with multi-selection options
			for (AbundanceSample sample : sampleList) {
				JCheckBox tmp_cbox = new JCheckBox();
				tmp_cbox.setText(sample.getID().toString());
				//tmp_cbox.addActionListener(new barChartListener(sample));
				samplePanel.add(tmp_cbox);
			}
		}
		
		//JScrollPane scrollPane = new JScrollPane(samplePanel); // wrap the panel in a scroll pane in case of long lists
		samplePanel.revalidate();
		samplePanel.repaint();
		setVisible(true);
	}

/*
 * CREATE ACTION LISTENERS
 */
	private class uploadListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				FetchData();
						
				// only add new buttons after FetchData() works
				promptLabel.setText("Select single or multi sample viewing option.");
				bottomPanel.add(singleButton); // single view
				bottomPanel.add(multiButton); // multi view
						
				samplePanel.removeAll();
				figurePanel.removeAll();
				setVisible(true);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}}
	}
	
	private class singleSampleListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			setSamplePanel(true);
			//setVisible(true);
		}
	}
	
	private class multiSampleListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			setSamplePanel(false);
			//setVisible(true);
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
			ChartPanel piePanel = makePieChart(sample);
			figurePanel.add(piePanel);
			
			// repaint panel with new chart object
			figurePanel.revalidate();
			figurePanel.repaint();
		}
	}
	
	/*
	private class barChartListener implements ActionListener {
		private AbundanceSample sample; // needs to be sample specific
		
		public barChartListener(AbundanceSample sample) {
			this.sample = sample;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// clear any existing chart(s)
			// figurePanel.removeAll();
			
			// call chart object
			ChartPanel barPanel = makeBarChart(sample);
			figurePanel.add(barPanel);
			
			// repaint panel with new chart object
			figurePanel.revalidate();
			figurePanel.repaint();
		}
	}
	*/
	
/*
 * CREATE ADDITIONAL METHODS
 * AS NEEDED TO INTERACT WITH ADDITIONAL SCRIPTS
 */
	private void FetchData() throws Exception
	{
		// create the file chooser
		final FileNameExtensionFilter tsv_filter = new FileNameExtensionFilter("CSV file", "csv"); // filter only tsv
		JFileChooser chooseFile = new JFileChooser();
		chooseFile.setFileFilter(tsv_filter);
		int return_value = chooseFile.showOpenDialog(getParent());
		
		// use selected file as parameter for reading function
		if (return_value == JFileChooser.APPROVE_OPTION) {
			File file = chooseFile.getSelectedFile();
			sampleList = AbundanceSample.readAbundanceTable(file);
		}
	}
	
	private ChartPanel makePieChart(AbundanceSample sample) {
		// set title using current Sample ID
		String title = "Composition of " + sample.getID() + " by Proportion";
		
		// set data
		HashMap<String, BigDecimal> propData = sample.getAbundanceProportions();
		DefaultPieDataset pieData = new DefaultPieDataset(); // fill with corresponding data
		
		for (String key : propData.keySet()) {
			pieData.setValue(key, propData.get(key));
		}
				
		// sort pieData
		pieData.sortByValues(SortOrder.DESCENDING);

		// make chart & panel
		JFreeChart chart = ChartFactory.createPieChart(title, pieData, 
				true, //legend 
				false, // tool tips 
				false); // URLsfalse
		
		ChartPanel piePanel = new ChartPanel(chart);
		return piePanel;
		
	}

	public static void main(String[] args)
	{
		new GenDisplay("SeqSumm");
	}
}
