package health;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;


public class TwitterHealthPanel extends JPanel {
	
	private static final Integer[] MY_REFRESH_NUMBER = {10, 100, 3000, 5000, 10000, 20000};
	private static final String[] MY_REFRESH_TIME = {"5 Seconds", "1 Minutes", "30 Minutes" ,
		"1 Hours", "5 hours", "12 Hours", "1 Day"};
	
	private TwitterHealthMachine myMachine;
	
	private JScrollPane myScrollPane;
	private JTextPane myTextPanel;
	private JPanel myControlPanel;
	
	private JCheckBox myAutoRefresh;
	private JComboBox<Integer> myRefreshNumber;
	private JComboBox<String> myRefreshTime;
	
	private JLabel myNumber;
	private JLabel myTime;
	
	
	private JButton myRefresh;
	private JButton myGet;
	private JButton mySave;
	
	
	
	
	private int myChoosenNumber = 10;
	private String myChoosenTime = "5 Seconds";
	private String myReport;
	
	private ButtonListener myListener;
	private Timer myTimer;
	
	private JFileChooser myFilePicker;
	
	public TwitterHealthPanel() {
		
		myMachine = new TwitterHealthMachine("StslpRKhCwJ4V6GRCSDzDsRCe",
				"445pzWwNmIu5PJfXXoTKUUSIkyXdl9FuVV5IOT4AoS73IKeh4n",
				"122779575-q5PgoMy2yextJKpa7Ei0ux3kxV06A5yXIWsMPRLp",
				"x092uSGnIN8i7T7KAu0M7WcbLrWlGXVffekviqQ2eYdIC");
		myListener = new ButtonListener();
		myTimer = new Timer(5000, new TimeListener());
		configurePanel();
	}
	
	private void configurePanel() {
		myTextPanel = new JTextPane();
		myTextPanel.setEditable(false);
		myTextPanel.setPreferredSize(new Dimension(300, 400));
		myScrollPane = new JScrollPane(myTextPanel);
		add(myScrollPane, BorderLayout.CENTER);
		
		myControlPanel = new JPanel();
		myControlPanel.setLayout(new GridLayout(8, 1));
		
		myAutoRefresh = new JCheckBox("Auto Refresh");
		myRefreshNumber =  new JComboBox<Integer>(MY_REFRESH_NUMBER);
		myRefreshTime = new JComboBox<String>(MY_REFRESH_TIME);
		
		myRefresh = new JButton("Refresh");
		myGet = new JButton("Get");
		mySave = new JButton("Save Report");
		mySave.setEnabled(false);
		
		myNumber = new JLabel("Number of Tweets");
		myTime = new JLabel("Time Interval");
		
		myAutoRefresh.addActionListener(myListener);
		myRefreshNumber.addActionListener(myListener);
		myRefreshTime.addActionListener(myListener);
		myRefresh.addActionListener(myListener);
		myGet.addActionListener(myListener);
		mySave.addActionListener(myListener);
		
		myControlPanel.add(myAutoRefresh);
		myControlPanel.add(myNumber);
		myControlPanel.add(myRefreshNumber);
		myControlPanel.add(myTime);
		myControlPanel.add(myRefreshTime);
		myControlPanel.add(myRefresh);
		myControlPanel.add(myGet);
		myControlPanel.add(mySave);
		add(myControlPanel, BorderLayout.EAST);
	}
	
	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent anEvent) {
			
			if (anEvent.getSource() == myAutoRefresh) {
				if (myAutoRefresh.isSelected()) {
					//checkedmy
					
					
					
					
					String message = "Do you want to start auto refresh "
					+ myChoosenNumber + "\nTweets for every " + myChoosenTime + " ?";
					int decision = JOptionPane.showConfirmDialog(null,
							message, "Meaasge", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (decision == JOptionPane.YES_OPTION) {
						myRefreshNumber.setEnabled(false);
						myRefreshTime.setEnabled(false);
						myRefresh.setEnabled(false);
						myGet.setEnabled(false);
						String choosenTime = myChoosenTime = MY_REFRESH_TIME[myRefreshTime.getSelectedIndex()];
						
						if (choosenTime.equals("5 Seconds")) {
							myTimer.setDelay(5000);
						} else if (choosenTime.equals("1 Minutes")) {
							myTimer.setDelay(60000);
						} else if (choosenTime.equals("30 Minutes")) {
							myTimer.setDelay(1800000);
						} else if (choosenTime.equals("1 Hours")) {
							myTimer.setDelay(3600000);
						} else if (choosenTime.equals("5 hours")) {
							myTimer.setDelay(18000000);
						} else if (choosenTime.equals("12 Hours")) {
							myTimer.setDelay(43200000);
						} else {
							myTimer.setDelay(86400000);
						}
						//System.out.println("finish setting time: " + myTimer.getDelay());
						myTimer.start();
						//System.out.println("finish start time: " + myTimer.isRunning());
					} else {
						myAutoRefresh.setSelected(false);
					}
					
				} else {
					//unchecked
					
					
					String message = "Do you want to cancel auto refresh?";
					int decision = JOptionPane.showConfirmDialog(null,
							message, "Meaasge", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (decision == JOptionPane.YES_OPTION) {
						myRefreshNumber.setEnabled(true);
						myRefreshTime.setEnabled(true);
						myGet.setEnabled(true);
						myRefresh.setEnabled(true);
						myTimer.stop();
						
					} else {
						myAutoRefresh.setSelected(true);
					}
					
				}
			}  else if (anEvent.getSource() == myRefreshNumber) {
				myChoosenNumber = MY_REFRESH_NUMBER[myRefreshNumber.getSelectedIndex()];
				System.out.println("Number set: " + myChoosenNumber);
			} else if (anEvent.getSource() == myRefreshTime) {
				myChoosenTime = MY_REFRESH_TIME[myRefreshTime.getSelectedIndex()];
				System.out.println("Time set: " + myChoosenTime);
			} else if (anEvent.getSource() == myRefresh) {
				myMachine.refresh(myChoosenNumber);
				System.out.println("Refresh " + myChoosenNumber);
			} else if (anEvent.getSource() == myGet) {
				List<String> mostTen = myMachine.getMostTen();
				
				
				/*//for test=======================================
				List<String> mostTen = new ArrayList<String>();
				mostTen.add("topic 1");
				mostTen.add("topic 2");
				mostTen.add("topic 3");
				mostTen.add("topic 4");
				mostTen.add("topic 5");
				mostTen.add("topic 6");
				mostTen.add("topic 7");
				mostTen.add("topic 8");
				mostTen.add("topic 9");
				mostTen.add("topic 10");
				//for test=======================================
*/				
				StringBuilder tempString = new StringBuilder();
				Calendar current = Calendar.getInstance();
				tempString.append("Refresh time: " + System.lineSeparator());
				tempString.append("============================== " + System.lineSeparator());
				tempString.append(current.get(Calendar.MONTH + 1) + "/");
				tempString.append(current.get(Calendar.DAY_OF_MONTH) + "/");
				tempString.append(current.get(Calendar.YEAR) + "   ");
				tempString.append(current.get(Calendar.HOUR_OF_DAY) + ":");
				tempString.append(current.get(Calendar.MINUTE) + ":");
				tempString.append(current.get(Calendar.SECOND) + ".");
				tempString.append(current.get(Calendar.MILLISECOND) + System.lineSeparator());
				tempString.append(current.getTimeZone().getDisplayName() + System.lineSeparator());
				tempString.append("============================== " + System.lineSeparator());
				tempString.append("Top 10 Hottest topics:" + System.lineSeparator());
				tempString.append("============================== " + System.lineSeparator());
				for (int i = 0; i < mostTen.size(); i++)
					tempString.append((i + 1) + ": "+ mostTen.get(i) + System.lineSeparator());
				//System.out.println(tempString.toString());
				myTextPanel.setText(tempString.toString());
				myReport = tempString.toString();
				mySave.setEnabled(true);
				
			} else if (anEvent.getSource() == mySave) {
				if (myFilePicker == null)
	            {
	                myFilePicker = new JFileChooser();
	            }
	            
	            myFilePicker.resetChoosableFileFilters();
	            
	            int select = -1; 
	            myFilePicker.setFileFilter(new FileNameExtensionFilter("TEXT file (*.txt)", "txt"));
	            select = myFilePicker.showSaveDialog(null);
	            File result;
	            if (select == JFileChooser.APPROVE_OPTION)
	            { 
	                result = myFilePicker.getSelectedFile();
	                if (result == null)
	                {
	                    return;
	                }
	                
	                PrintWriter writer = null;
	                try {
	                    writer = new PrintWriter(new FileOutputStream(result.getAbsolutePath() + ".txt"));
	                    writer.println(myReport);
	                    
	                } catch (final NoSuchElementException ex) {
	                    System.out.println("Output folder not found: " + ex.getMessage());
	                } catch (final FileNotFoundException ex) {
	                    System.out.println("Output file not found: " + ex.getMessage());
	                } finally {
	                    if (writer != null) {
	                        writer.close();
	                    }
	                }
	            }
			} else {
			
				System.out.println("never gonna happen");
			}
			
		}
		
	}
	
	private class TimeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent anEvent) {
			System.out.println("timer evernt");
			myMachine.refresh(myChoosenNumber);
			System.out.println("Refresh " + myChoosenNumber);
			
		}
		
	}
}
