//Alex Johnson
//Weather record keeper final java project
//5/6/15
import java.awt.EventQueue;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.sql.*;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import java.awt.CardLayout;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import net.proteanit.sql.DbUtils;

public class Weather 
{
	//declare variables and JFrame objects
	private JFrame frmSaintPaulWeather;
	private JPanel main;
	private JLabel lblBackImg1;
	private JButton btnViewRecords;
	private JButton btnAddRecord;
	private JButton btnCalculate;
	private JLabel lblLowTemperature;
	private JTextField txtLowTemperature;
	private JLabel lblHighTemperature;
	private JTextField txtHighTemperature;
	private JLabel lblCloudCover;
	private JTextField txtDate;
	private JLabel lblDate;
	private JLabel lblTemperature;
	private JTextField txtTemperature;
	private JLabel lblWindSpeed;
	private JTextField txtWindSpeed;
	private JLabel lblHumidity;
	private JTextField txtHumidity;
	private int temp;
	private int windSpeed;
	private int humidity;
	private int highTemp;
	private int lowTemp;
	private JPanel winterPanel;
	private JLabel lblBackImg2;
	private double windChillNum;
	private double heatIndexNum;
	private double heatIndexAvg;
	private JTextArea windChill = new JTextArea();
	private JPanel heatIndex;
	private JLabel lblBackImgDesert;
	private JLabel lblBackImgSummer;
	private JLabel lblBackImgRainforest;
	private JTextArea heatIndexOutput;
	private JButton closeSummerPanel;
	static Connection con;
	private JPanel SQLTable;
	private JLabel lblBackImgNrthLights;
	private JTable table;
	private JScrollPane scrollPane;
	private JButton btnEditData;
	private JButton btnCloseSQLTable;
	private JButton btnClearData;
	
	//Launch the application.
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					Weather window = new Weather();
					window.frmSaintPaulWeather.setVisible(true);
				} catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
	}

	//Create the application
	public Weather() 
	{
		initialize();
	}

	//Initialize the contents of the frame
	private void initialize() 
	{
		//connect to the SqlConnection class to connect to SQL Server
		try
		{
			con = SqlConnection.dbConnector();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		frmSaintPaulWeather = new JFrame();
		frmSaintPaulWeather.setFont(new Font("Georgia", Font.PLAIN, 12));
		frmSaintPaulWeather.setTitle("Saint Paul Weather Program");
		frmSaintPaulWeather.setBounds(100, 100, 750, 475);
		frmSaintPaulWeather.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSaintPaulWeather.getContentPane().setLayout(new CardLayout(0, 0));
		
		main = new JPanel();
		frmSaintPaulWeather.getContentPane().add(main, "name_132166199038492");
		main.setLayout(null);
		
		JComboBox cloudCoverBox = new JComboBox();
		cloudCoverBox.setFont(new Font("Georgia", Font.PLAIN, 11));
		cloudCoverBox.setBounds(44, 289, 176, 20);
		main.add(cloudCoverBox);
		cloudCoverBox.addItem("(Please select an option)");
		cloudCoverBox.addItem("Sunny");
		cloudCoverBox.addItem("Partly Cloudy");
		cloudCoverBox.addItem("Overcast");
		
		btnClearData = new JButton("Clear Data");
		btnClearData.setToolTipText("clear data from all fields");
		btnClearData.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				//clears data from all fields
				txtDate.setText("");
				txtTemperature.setText("");
				txtHighTemperature.setText("");
				txtLowTemperature.setText("");
				txtWindSpeed.setText("");
				txtHumidity.setText("");
				cloudCoverBox.setSelectedIndex(0);
			}
		});
		btnClearData.setFont(new Font("Georgia", Font.PLAIN, 11));
		btnClearData.setBounds(188, 363, 107, 44);
		main.add(btnClearData);
		
		btnViewRecords = new JButton("View Records");
		btnViewRecords.setToolTipText("view records stored in SQL Server");
		btnViewRecords.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				//changes which JPanel is visible
				main.setVisible(false);
				SQLTable.setVisible(true);
				try
				{
					//connects to SQL Server, selects all contents from a row, and inserts them into the table
					String query = "Select * from WeatherTable";
					PreparedStatement pst = con.prepareStatement(query);
					ResultSet rs = pst.executeQuery();
					table.setModel(DbUtils.resultSetToTableModel(rs));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		btnViewRecords.setFont(new Font("Georgia", Font.PLAIN, 11));
		btnViewRecords.setBounds(573, 363, 122, 44);
		main.add(btnViewRecords);
		
		btnAddRecord = new JButton("Add / Update Record");
		btnAddRecord.setToolTipText("add record to SQL Server database");
		btnAddRecord.setFont(new Font("Georgia", Font.PLAIN, 11));
		btnAddRecord.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				//creates a simple date format to make sure that the input is a date
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd");
				//makes the date check strict to catch any type of error
				sdf.setLenient(false);
				boolean invalidInput = false;
				int invalidCounter = 0;
				String invalidOutput;
				String dateInvalid = "";
				String highTempInvalid = "";
				String lowTempInvalid = "";
				String windSpeedInvalid = "";
				String humidityInvalid = "";
				int cloudCoverIndex = cloudCoverBox.getSelectedIndex();
				
				//checks to make sure that all inputs are valid for the expected format of the input
				try 
				{
					//if not valid, it will throw ParseException
					Date date = sdf.parse(txtDate.getText());
				} 
				catch (ParseException e) 
				{
					//sets the output to use if any error is found in the input
					dateInvalid = " Date";
					invalidCounter += 1;
					invalidInput = true;
				}
				
				//checks to see if field is blank
				if (!txtHighTemperature.getText().equals(""))
				{
					try 
					{
						highTemp = Integer.parseInt(txtHighTemperature.getText());
					}
					catch (NumberFormatException ex)
					{
						//if an error has already been found and is added to the input
						if (invalidCounter > 0)
						{
							highTempInvalid = " and High Temperature";
						}
						else
						{
							highTempInvalid = " High Temperature";
						}
							invalidCounter += 1;
							invalidInput = true;
					}
				}
				
				if (!txtLowTemperature.getText().equals(""))
				{
					try 
					{
						lowTemp = Integer.parseInt(txtLowTemperature.getText());
					}
					catch (NumberFormatException ex)
					{
						if (invalidCounter > 0)
						{
							lowTempInvalid = " and Low Temperature";
						}
						else
						{
							lowTempInvalid = " Low Temperature";
						}
							invalidCounter += 1;
							invalidInput = true;
					}
				}
				
				if (!txtWindSpeed.getText().equals(""))
				{
					try
					{
						windSpeed = Integer.parseInt(txtWindSpeed.getText());
					}
					catch (NumberFormatException ex)
					{
						if (invalidCounter > 0)
						{
							windSpeedInvalid = " and Wind Speed";
						}
						else
						{
							windSpeedInvalid = " Wind Speed";
						}
							invalidCounter += 1;
							invalidInput = true;
					}
				}
				
				if (!txtHumidity.getText().equals(""))
				{
					try
					{
						humidity = Integer.parseInt(txtHumidity.getText());
					}
					catch (NumberFormatException ex)
					{
						if (invalidCounter > 0)
						{
							humidityInvalid = " and Humidty";
						}
						else
						{
							humidityInvalid = " Humidity";
						}
							invalidCounter += 1;
							invalidInput = true;
					}
				}
				
				//if there is invalid input output the correct response
				if (invalidInput == true)
				{
					//checks to see if date field is blank
					if (txtDate.getText().equals(""))
					{
						JOptionPane.showMessageDialog(null, "Please insert data into Date");
					}
					else
					{
						//outputs only which input is invalid
						invalidOutput = "Sorry, the data you entered into" + dateInvalid + highTempInvalid + lowTempInvalid + humidityInvalid + windSpeedInvalid + " is invalid.";
						JOptionPane.showMessageDialog(null, invalidOutput);
					}
				}
				else
				{
					try
					{
						//inserts all data into the SQL server database
						String query = "insert into WeatherTable (RecordDate, HighTemp, LowTemp, WindSpeed, Humidity, CloudCover) values (?, ?, ?, ?, ?, ?)";
						PreparedStatement pst = con.prepareStatement(query);
						pst.setString(1, txtDate.getText());
						pst.setString(2, txtHighTemperature.getText());
						pst.setString(3, txtLowTemperature.getText());
						pst.setString(4, txtWindSpeed.getText());
						pst.setString(5, txtHumidity.getText());
						//checks to see which item is selected in the JComboBox
						if (cloudCoverIndex == 0)
						{
							pst.setString(6, null);
						}
						else if (cloudCoverIndex == 1)
						{
							pst.setString(6, "Sunny");
						}
						else if (cloudCoverIndex == 2)
						{
							pst.setString(6, "Partly Cloudy");
						}
						else if (cloudCoverIndex == 3)
						{
							pst.setString(6, "Overcast");
						}
						pst.execute();
						pst.close();
						JOptionPane.showMessageDialog(null, "The weather data you have entered has been saved.");
						//clears all data fields
						txtDate.setText("");
						txtTemperature.setText("");
						txtHighTemperature.setText("");
						txtLowTemperature.setText("");
						txtWindSpeed.setText("");
						txtHumidity.setText("");
						cloudCoverBox.setSelectedIndex(0);
					}
					catch (Exception e1)
					{
						if (con == null)
						{
							//if the connection is not successful output this message
							JOptionPane.showMessageDialog(null, "Something went wrong when trying to save data to database.");
						}
						else
						{
							int option;
							//if the date that is input has already been used, ask the user if they would like to update the record
							option = JOptionPane.showConfirmDialog(null, "This date has already been used. Would you like to update the record?");
							if (option == 0)
							{
								try
								{
									String query = "Update WeatherTable SET HighTemp = (?), LowTemp = (?), WindSpeed = (?), Humidity = (?), CloudCover = (?) where RecordDate = ?";
									PreparedStatement pst = con.prepareStatement(query);
									pst.setString(1, txtHighTemperature.getText());
									pst.setString(2, txtLowTemperature.getText());
									pst.setString(3, txtWindSpeed.getText());
									pst.setString(4, txtHumidity.getText());
									if (cloudCoverIndex == 0)
									{
										pst.setString(5, null);
									}
									else if (cloudCoverIndex == 1)
									{
										pst.setString(5, "Sunny");
									}
									else if (cloudCoverIndex == 2)
									{
										pst.setString(5, "Partly Cloudy");
									}
									else if (cloudCoverIndex == 3)
									{
										pst.setString(5, "Overcast");
									}
									pst.setString(6, txtDate.getText());
									pst.execute();
									pst.close();
									JOptionPane.showMessageDialog(null, "The weather data you have entered has been saved.");
									//clears all data fields
									txtDate.setText("");
									txtTemperature.setText("");
									txtHighTemperature.setText("");
									txtLowTemperature.setText("");
									txtWindSpeed.setText("");
									txtHumidity.setText("");
									cloudCoverBox.setSelectedIndex(0);
								}
								catch (Exception e)
								{
									JOptionPane.showMessageDialog(null, "Something went wrong when trying to save data to database.");
								}
							}
						}
					}
				}
			}
		});
		btnAddRecord.setBounds(349, 363, 162, 44);
		main.add(btnAddRecord);
		
		btnCalculate = new JButton("Calculate");
		btnCalculate.setToolTipText("calculate wind chill or heat index");
		btnCalculate.setFont(new Font("Georgia", Font.PLAIN, 11));
		btnCalculate.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				boolean blankInput = false;
				boolean invalidInput = false;
				int blankCounter = 0;
				int invalidCounter = 0;
				String invalidOutput;
				String blankOutput;
				String tempBlank = "";
				String windSpeedBlank = "";
				String humidityBlank = "";
				String tempInvalid = "";
				String windSpeedInvalid = "";
				String humidityInvalid = "";
				
				//checks to see if input required to calculate wind chill or heat index are invalid
				try 
				{
					temp = Integer.parseInt(txtTemperature.getText());
				}
				catch (NumberFormatException ex)
				{
					tempInvalid = " Temperature";
					invalidCounter += 1;
					invalidInput = true;
				}
				
				try
				{
					windSpeed = Integer.parseInt(txtWindSpeed.getText());
				}
				catch (NumberFormatException ex)
				{
					if (invalidCounter > 0)
					{
						windSpeedInvalid = " and Wind Speed";
					}
					else
					{
						windSpeedInvalid = " Wind Speed";
					}
					invalidCounter += 1;
					invalidInput = true;
				}
				
				try
				{
					humidity = Integer.parseInt(txtHumidity.getText());
				}
				catch (NumberFormatException ex)
				{
					if (invalidCounter > 0)
					{
						humidityInvalid = " and Humidty";
					}
					else
					{
						humidityInvalid = " Humidity";
					}
					invalidCounter += 1;
					invalidInput = true;
				}
				
				//checks to see if any fields required to calculate wind chill or heat index are blank
				if (txtTemperature.getText().equals(""))
				{
					tempBlank = " Temperature";
					blankCounter += 1;
					blankInput = true;
				}
				
				if (txtWindSpeed.getText().equals(""))
				{
					if (blankCounter > 0)
					{
						windSpeedBlank = " and Wind Speed";
					}
					else
					{
						windSpeedBlank = " Wind Speed";
					}
					blankCounter += 1;
					blankInput = true;
				}
				
				if (txtHumidity.getText().equals(""))
				{
					if (blankCounter > 0)
					{
						humidityBlank = " and Humidty";
					}
					else
					{
						humidityBlank = " Humidity";
					}
					blankCounter += 1;
					blankInput = true;
					
				}
				
				//checks to see if any input is invalid or blank and outputs the correct response
				if (invalidInput == true)
				{
					if (blankInput == true)
					{
						blankOutput = "Please insert data into" + tempBlank + windSpeedBlank + humidityBlank + ".";
						JOptionPane.showMessageDialog(null, blankOutput);
					}
					else
					{
						invalidOutput = "Sorry, the data you entered into" + tempInvalid + windSpeedInvalid + humidityInvalid + " is invalid.";
						JOptionPane.showMessageDialog(null, invalidOutput);
					}
				}
				else
				{
					//calculates wind chill from input where applicable
					if (temp <= 50 && windSpeed > 3)
					{
						main.setVisible(false);
						winterPanel.setVisible(true);
						windChillNum = 35.74 + (0.6215 * temp) - (35.75 * Math.pow(windSpeed, 0.16)) + ((0.4275 * temp) *Math.pow(windSpeed, 0.16));
						windChill.setText(" The wind Chill for a temperture of " + temp + "°F and a wind speed of " + windSpeed + "(mph) is "
						+ String.format("%.2f", windChillNum) + "°F.");
					}
					else if (temp <= 50 && windSpeed < 3)
					{
						main.setVisible(false);
						winterPanel.setVisible(true);
						windChill.setText(" Sorry but the wind speed you entered is too low to calculate wind chill.");
						
					}
					//calculates heat index where applicable
					else if (temp > 50)
					{
						main.setVisible(false);
						heatIndex.setVisible(true);
						heatIndexNum = 0.5 * (temp + 61 + ((temp - 68) * 1.2) + (humidity * 0.094));
						heatIndexAvg = (heatIndexNum + temp) / 2;
						if (heatIndexAvg > 80)
						{
							//formula is more complicated if temperature is above 80 degrees
							heatIndexNum = -42.379 + (2.04901523 * temp) + (10.14333127 * humidity) - (0.22475541 * temp * humidity) - (0.00683783 * temp * temp) 
									- (0.05481717 * humidity * humidity) + (0.00122874 * temp * temp * humidity) + (0.00085282 * temp * humidity * humidity) 
									- (0.00000199 * temp * temp * humidity * humidity);
							if ((temp >= 80 && temp <= 112) && (humidity < 13))
							{
								//assigns the appropriate background image to the summer panel depending on the input
								lblBackImgRainforest.setVisible(false);
								lblBackImgSummer.setVisible(false);
								//subtracts an amount from heat index where applicable
								heatIndexNum -= ((13 - humidity) / 4) * Math.sqrt((17-Math.abs(temp - 95)) / 17);
							}
							else if ((temp >= 80 && temp <= 87) && (humidity > 85))
							{
								//adds an amount to heat index where applicable
								lblBackImgSummer.setVisible(false);
								lblBackImgDesert.setVisible(false);
								heatIndexNum += ((humidity - 85) / 10) * ((87 - temp) / 5);
							}
						}
						heatIndexOutput.setText(" The heat index for a temperature of " + temp + "°F and a humidity of " + humidity + "% is "
								+ String.format("%.0f", heatIndexNum) + "°F.");
					}

				}
			}
		});
		btnCalculate.setBounds(44, 363, 89, 44);
		main.add(btnCalculate);
		
		lblLowTemperature = new JLabel("Low Temperature (F)");
		lblLowTemperature.setFont(new Font("Georgia", Font.PLAIN, 11));
		lblLowTemperature.setForeground(Color.WHITE);
		lblLowTemperature.setBounds(44, 142, 128, 14);
		main.add(lblLowTemperature);
		
		txtLowTemperature = new JTextField();
		txtLowTemperature.setColumns(10);
		txtLowTemperature.setBounds(44, 167, 107, 20);
		main.add(txtLowTemperature);
		
		lblHighTemperature = new JLabel("High Temperature (F)");
		lblHighTemperature.setFont(new Font("Georgia", Font.PLAIN, 11));
		lblHighTemperature.setForeground(Color.WHITE);
		lblHighTemperature.setBounds(540, 14, 142, 14);
		main.add(lblHighTemperature);
		
		txtHighTemperature = new JTextField();
		txtHighTemperature.setColumns(10);
		txtHighTemperature.setBounds(540, 39, 107, 20);
		main.add(txtHighTemperature);
		
		lblCloudCover = new JLabel("Cloud Cover");
		lblCloudCover.setFont(new Font("Georgia", Font.PLAIN, 11));
		lblCloudCover.setForeground(Color.WHITE);
		lblCloudCover.setBounds(44, 264, 95, 14);
		main.add(lblCloudCover);
		
		txtDate = new JTextField();
		
		txtDate.setColumns(10);
		txtDate.setBounds(44, 39, 107, 20);
		main.add(txtDate);
		
		lblDate = new JLabel("Date (yyyy/mm/dd)");
		lblDate.setFont(new Font("Georgia", Font.PLAIN, 11));
		lblDate.setForeground(Color.WHITE);
		lblDate.setBounds(44, 14, 144, 14);
		main.add(lblDate);
		
		lblTemperature = new JLabel("Temperature (F)");
		lblTemperature.setFont(new Font("Georgia", Font.PLAIN, 11));
		lblTemperature.setForeground(Color.WHITE);
		lblTemperature.setBounds(279, 14, 107, 14);
		main.add(lblTemperature);
		
		txtTemperature = new JTextField();
		
		txtTemperature.setColumns(10);
		txtTemperature.setBounds(279, 39, 107, 20);
		main.add(txtTemperature);
		
		lblWindSpeed = new JLabel("Wind Speed (MPH)");
		lblWindSpeed.setFont(new Font("Georgia", Font.PLAIN, 11));
		lblWindSpeed.setForeground(Color.WHITE);
		lblWindSpeed.setBounds(540, 142, 122, 14);
		main.add(lblWindSpeed);
		
		txtWindSpeed = new JTextField();
		txtWindSpeed.setColumns(10);
		txtWindSpeed.setBounds(540, 167, 107, 20);
		main.add(txtWindSpeed);
		
		lblHumidity = new JLabel("Humidity");
		lblHumidity.setFont(new Font("Georgia", Font.PLAIN, 11));
		lblHumidity.setForeground(Color.WHITE);
		lblHumidity.setBounds(279, 142, 95, 14);
		main.add(lblHumidity);
		
		txtHumidity = new JTextField();
		txtHumidity.setColumns(10);
		txtHumidity.setBounds(279, 167, 144, 20);
		main.add(txtHumidity);
		
		lblBackImg1 = new JLabel("");
		lblBackImg1.setIcon(new ImageIcon(Weather.class.getResource("/Image/sunshine1.jpg")));
		lblBackImg1.setBounds(0, 0, 734, 437);
		main.add(lblBackImg1);
		
		winterPanel = new JPanel();
		frmSaintPaulWeather.getContentPane().add(winterPanel, "name_477842976668225");
		winterPanel.setLayout(null);
		
		JButton closeWinterPanel = new JButton("Back to Main");
		closeWinterPanel.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0)
			{
				//closes the winter panel and returns the user to the main panel
				winterPanel.setVisible(false);
				main.setVisible(true);
			}
		});
		closeWinterPanel.setFont(new Font("Georgia", Font.PLAIN, 11));
		closeWinterPanel.setBounds(290, 143, 134, 44);
		winterPanel.add(closeWinterPanel);
		
		windChill.setFont(new Font("Georgia", Font.PLAIN, 15));
		windChill.setBounds(81, 105, 584, 27);
		winterPanel.add(windChill);
		
		lblBackImg2 = new JLabel("");
		lblBackImg2.setIcon(new ImageIcon(Weather.class.getResource("/Image/Winter Scene.jpg")));
		lblBackImg2.setBounds(0, 0, 750, 475);
		winterPanel.add(lblBackImg2);
		
		heatIndex = new JPanel();
		frmSaintPaulWeather.getContentPane().add(heatIndex, "name_905952478483880");
		heatIndex.setLayout(null);
		
		closeSummerPanel = new JButton("Back to Main");
		closeSummerPanel.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				//returns the summer panel to normal and returns the user to the main panel
				lblBackImgDesert.setVisible(true);
				lblBackImgRainforest.setVisible(true);
				lblBackImgSummer.setVisible(true);
				heatIndex.setVisible(false);
				main.setVisible(true);
			}
		});
		closeSummerPanel.setFont(new Font("Georgia", Font.PLAIN, 11));
		closeSummerPanel.setBounds(301, 145, 134, 44);
		heatIndex.add(closeSummerPanel);
		
		heatIndexOutput = new JTextArea();
		heatIndexOutput.setFont(new Font("Georgia", Font.PLAIN, 15));
		heatIndexOutput.setBounds(84, 107, 584, 27);
		heatIndex.add(heatIndexOutput);
		
		lblBackImgSummer = new JLabel("New label");
		lblBackImgSummer.setIcon(new ImageIcon(Weather.class.getResource("/Image/summer.jpg")));
		lblBackImgSummer.setBounds(0, 0, 734, 437);
		heatIndex.add(lblBackImgSummer);
		
		lblBackImgDesert = new JLabel("New label");
		lblBackImgDesert.setIcon(new ImageIcon(Weather.class.getResource("/Image/desert.jpg")));
		lblBackImgDesert.setBounds(0, 0, 734, 437);
		heatIndex.add(lblBackImgDesert);
		
		lblBackImgRainforest = new JLabel("New label");
		lblBackImgRainforest.setIcon(new ImageIcon(Weather.class.getResource("/Image/rainforest.jpeg")));
		lblBackImgRainforest.setBounds(0, 0, 734, 437);
		heatIndex.add(lblBackImgRainforest);
		
		SQLTable = new JPanel();
		frmSaintPaulWeather.getContentPane().add(SQLTable, "name_1351838177384046");
		SQLTable.setLayout(null);
		
		btnCloseSQLTable = new JButton("Back to Main");
		btnCloseSQLTable.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				//returns the user to the main panel from the SQL table panel
				SQLTable.setVisible(false);
				main.setVisible(true);
			}
		});
		
		JButton btnDeleteData = new JButton("Delete Data");
		btnDeleteData.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				try
				{
					int optionCheck;
					//make sure user intended to delete the record and if yes, delete the selected record
					optionCheck = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the recorded data for the date of " 
							+ table.getValueAt(table.getSelectedRow(), 0) + "?");
					if (optionCheck == 0)
					{
						String query = "delete from WeatherTable where RecordDate = ?";
						PreparedStatement pst = con.prepareStatement(query);
						pst.setString(1, table.getValueAt(table.getSelectedRow(), 0).toString());
						pst.execute();
						pst.close();
						try
						{
							//instantly updates the table
							String query2 = "Select * from WeatherTable";
							PreparedStatement pst1 = con.prepareStatement(query2);
							ResultSet rs = pst1.executeQuery();
							table.setModel(DbUtils.resultSetToTableModel(rs));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						JOptionPane.showMessageDialog(null, "The data has been deleted.");
					}
				}
				catch (Exception ex)
				{
					JOptionPane.showMessageDialog(null, "Sorry, there was an error while trying to delete the data.");
				}
			}
		});
		btnDeleteData.setFont(new Font("Georgia", Font.PLAIN, 11));
		btnDeleteData.setBounds(292, 355, 110, 44);
		SQLTable.add(btnDeleteData);
		btnCloseSQLTable.setFont(new Font("Georgia", Font.PLAIN, 11));
		btnCloseSQLTable.setBounds(538, 355, 134, 44);
		SQLTable.add(btnCloseSQLTable);
		
		btnEditData = new JButton("Edit Data");
		btnEditData.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				//brings all of the data from the selected row back to the main panel so the user can edit it
				int cloudCoverIndex = 0;
				txtDate.setText(table.getValueAt(table.getSelectedRow(), 0).toString());
				txtHighTemperature.setText(table.getValueAt(table.getSelectedRow(), 1).toString());
				txtLowTemperature.setText(table.getValueAt(table.getSelectedRow(), 2).toString());
				txtWindSpeed.setText(table.getValueAt(table.getSelectedRow(), 3).toString());
				txtHumidity.setText(table.getValueAt(table.getSelectedRow(), 4).toString());
				//assigns the JComboBox the appropriate index based on the data saved in the table
				if (table.getValueAt(table.getSelectedRow(), 5).toString().equals("Sunny"))
				{
					cloudCoverIndex = 1;
				}
				else if (table.getValueAt(table.getSelectedRow(), 5).toString().equals("Partly Cloudy"))
				{
					cloudCoverIndex = 2;
				}
				else if (table.getValueAt(table.getSelectedRow(), 5).toString().equals("Overcast"))
				{
					cloudCoverIndex = 3;
				}
				cloudCoverBox.setSelectedIndex(cloudCoverIndex);
				//returns the user to the main panel from the SQL Table panel
				SQLTable.setVisible(false);
				main.setVisible(true);
			}
		});
		btnEditData.setFont(new Font("Georgia", Font.PLAIN, 11));
		btnEditData.setBounds(46, 355, 110, 44);
		SQLTable.add(btnEditData);
		
		scrollPane = new JScrollPane();

		scrollPane.setBounds(46, 37, 626, 261);
		SQLTable.add(scrollPane);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		
		lblBackImgNrthLights = new JLabel("");
		lblBackImgNrthLights.setIcon(new ImageIcon(Weather.class.getResource("/Image/Northern Lights.jpg")));
		lblBackImgNrthLights.setBounds(0, 0, 734, 437);
		SQLTable.add(lblBackImgNrthLights);
	}
}