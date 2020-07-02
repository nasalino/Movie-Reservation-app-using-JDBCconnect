
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

//import from JSch (Java Secure Channel) jar file - http://www.jcraft.com/jsch/ 
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;


public class jdbcConnect {
	public  Session session;						// SSH tunnel session
	Connection connection;
	// Name : Ihemebiri Chinasa Martins
	// Student number: 2981843
	// Course: Relational Database
	
	/**
	 * Open SSH Tunnel to SSH server and forward the specified port on the local machine to the MySQL port on the MySQL server on the SSH server
	 * @param sshUser SSH username
	 * @param sshPassword SSH password
	 * @param sshHost hostname or IP of SSH server
	 * @param sshPort SSH port on SSH server
	 * @param remoteHost hostname or IP of MySQL server on SSH server (from the perspective of the SSH Server)
	 * @param localPort port on the local machine to be forwarded
	 * @param remotePort MySQL port on remoteHost 
	 */
	private void openSSHTunnel( String sshUser, String sshPassword, String sshHost, int sshPort, String remoteHost, int localPort, int remotePort ){
		try{
			final JSch jsch = new JSch();							// create a new Java Secure Channel
			session = jsch.getSession( sshUser, sshHost, sshPort);	// get the tunnel
			session.setPassword(sshPassword );						// set the password for the tunnel

			final Properties config = new Properties();				// create a properties object 
			config.put( "StrictHostKeyChecking", "no" );			// set some properties
			session.setConfig( config );							// set the properties object to the tunnel

			session.connect();										// open the tunnel
			System.out.println("\nSSH Connecting ***********************************************************************************************************************");
			System.out.println("Success: SSH tunnel open - you are connecting to "+sshHost+ "on port "+sshPort+ " with username " + sshUser);

			// set up port forwarding from a port on your local machine to a port on the MySQL server on the SSH server
			session.setPortForwardingL(localPort, remoteHost, remotePort);							
			// output a list of the ports being forwarded 
			
			System.out.println("Success: Port forwarded - You have forwared port "+ localPort + " on the local machine to port " + remotePort + " on " + remoteHost + " on " +sshHost);
		}
		catch(Exception e ){
			e.printStackTrace();
		}
	}
	
	/**
	 * Close SSH tunnel to a remote server
	 */
	private void closeSshTunnel(int localPort){
		try {
			// remove the port forwarding and output a status message
			System.out.println("\nSSH Connection Closing ******************************************************************************************************************");
			session.delPortForwardingL(localPort);
			System.out.println("Success: Port forwarding removed");
			// catch any exceptions	
		} catch (JSchException e) {
			System.out.println("Error: port forwarding removal issue");
			e.printStackTrace();
		}
		// disconnect the SSH tunnel
		session.disconnect();
		System.out.println("Success: SSH tunnel closed\n");
	}

	/**
	 * Open a connection with MySQL server. If there is an SSH Tunnel required it will open this too. 
	 */
	public void openConnection(String mysqlHost, int localPort, String mysqlDatabaseName, String mysqlUsername, String mysqlPassword){
		try{
			// create a new JDBC driver to facilitate the conversion of MySQL to java and vice versa
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();

			// connect to the MySQL database through the SSH tunnel you have created using the variable above
			String jdbcConnectionString = "jdbc:mysql://"+mysqlHost+":"+localPort+"/"+mysqlDatabaseName+"?user="+mysqlUsername+"&password="+mysqlPassword;
			System.out.println("\nMySQL Connecting *********************************************************************************************************************");
			System.out.println("JDBC connection string "+jdbcConnectionString);
			connection = DriverManager.getConnection(jdbcConnectionString);
			System.out.println("Connection:"+connection.toString());
			System.out.println("Success: MySQL connection open");

			// testing connection 
			//testConnection();

		}
		// catch various exceptions and print error messages
		catch (SQLException e){ 
			System.err.println("> SQLException: " + e.getMessage());
			e.printStackTrace();
		}
		catch (InstantiationException e) {
			System.err.println("> InstantiationException: " + e.getMessage());
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			System.err.println("> IllegalAccessException: " + e.getMessage());
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			System.err.println("> ClassNotFoundException: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void closeConnection(){
		System.out.println("\nMySQL Connection Closing ****************************************************************************************************************");
		try {
			connection.close(); // close database connection
			System.out.println("Success: MySQL connection closed.");
		} catch (SQLException e) {
			System.out.println("Error: Could not close MySQL connection");
			System.err.println(e);	
			e.printStackTrace();}
	}
	
	/**
	 * Test the connection by printing out everything in the Customer table.
	 */
	public void viewMovie()
	{
		
			
			try {
				Statement st = connection.createStatement(); 							// create an SQL statement
				ResultSet rs = st.executeQuery("SELECT * from Film");  // retrieve an SQL results set

				// output the results set to the user
				System.out.println("*******ALL Film details*******");

				while (rs.next()){
					int FilmID = rs.getInt("FilmID");
					String Title = rs.getString("Title");
					String RentalPrice = rs.getString("RentalPrice");
					String Kind = rs.getString("Kind");
					
					
					System.out.print(FilmID + " ");
					System.out.print(Title + " ");
					System.out.print(RentalPrice + " ");
					System.out.print(Kind + " \n");
				}
				
				if (st != null) {
					st.close();		//close the SQL statement
				}
				if (rs != null){	//close the Result Set
					rs.close();
				}


			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	
	public void viewReservations()
	{
		
			
			try {
				Statement st = connection.createStatement(); 							// create an SQL statement
				ResultSet rs = st.executeQuery("SELECT * from Film");  // retrieve an SQL results set

				// output the results set to the user
				System.out.println("//////ALL Reserved Film with Customers details//////");

				
				
				
				
				while (rs.next()){
					int CustomerID = rs.getInt("CustomerID");
					String FilmID = rs.getString("FilmID");
					
					
					
					System.out.print(CustomerID + " ");
					System.out.print(FilmID + " \n ");
					
					
				}
				
				if (st != null) {
					st.close();		//close the SQL statement
				}
				if (rs != null){	//close the Result Set
					rs.close();
				}


			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

public void CustomerRecords()
{

	try {Statement st = connection.createStatement(); 				// create an SQL statement
		ResultSet rs = st.executeQuery("SELECT * from Customer");  // retrieve an SQL results set

		// output the results set to the user
		System.out.println("///////All Customer Records///////");

		while (rs.next()){
			int CustomerID = rs.getInt("CustomerID");
			String Name = rs.getString("Name");
			String City = rs.getString("City");
			String Street = rs.getString("Street");
			String Address = rs.getString("Address");
			
			System.out.print(CustomerID + " ");
			System.out.print(Name + " ");
			System.out.print(City + " ");
			System.out.print(Street + " ");
			System.out.print(Address + " \n");

		}
		
		if (st != null) {
			st.close();		//close the SQL statement
		}
		if (rs != null){	//close the Result Set
			rs.close();
		}


	} catch (SQLException e) {
		e.printStackTrace();
	}
}

	public void CustomerDetails() {
		try {
			Statement st = connection.createStatement();
Scanner userInput = new Scanner (System.in);
				
			System.out.println("Input Your Name:");
			String CustName = userInput.nextLine();
			
			System.out.println("Input Your City:");
			String CustCity = userInput.nextLine();
			
			System.out.println("Input Your Street:");
			String CustStreet = userInput.nextLine();
			
			System.out.println("Input Your Address:");
			String CustAddress = userInput.nextLine();
			String st1= "INSERT INTO Customer ( Name, City, Street, Address ) VALUES('"+ CustName + "','" +CustCity + "','"+ CustStreet + "','"+ CustAddress + "')";
			
			

			System.out.println(st1);
			 st.executeUpdate(st1);
	
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
			public void ViewReservations() {
try {
			
			
			Statement st = connection.createStatement(); 
			
			Scanner myInput= new Scanner (System.in);
			
			ResultSet rs = st.executeQuery("SELECT * from Film, Reserved;");  // retrieve an SQL results set

	
			
			CustomerRecords();
			
			System.out.println("  ");
			System.out.print("Input Customer ID:");
			int custid = myInput.nextInt();
			
			rs=	st.executeQuery("select * FROM Reserved JOIN Film ON Reserved.FilmID = Film.FilmID WHERE CustomerID = " + custid);
		
			while (rs.next()){
				int FilmID = rs.getInt("FilmID");
				String Title = rs.getString("Title");
				String RentalPrice = rs.getString("RentalPrice");
				String Kind = rs.getString("Kind");
				
				
				System.out.print(FilmID + " ");
				System.out.print(Title + " ");
				System.out.print(RentalPrice + " ");
				System.out.print(Kind + " \n");
				
			}
			while (rs.next()){
				int CustomerID = rs.getInt("CustomerID");
				String FilmID = rs.getString("FilmID");
				
				
				
				System.out.print(CustomerID + " ");
				System.out.print(FilmID + " \n ");
				
				
			}
			
			
			if (st != null) {
				st.close();		//close the SQL statement
			}
			if (rs != null){	//close the Result Set
				rs.close();
			}
			


		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
public void MakeReservation() {
		
		try {
			Scanner userInput = new Scanner (System.in);
			Statement st = connection.createStatement();
			
			// ResultSet rs = st.executeQuery("SELECT * from Customer;"); 
			 CustomerRecords();
			
			
			
			 System.out.println("  ");
			 
			System.out.println("//////Input CustomerID//////");
			int Cus_ID = userInput.nextInt();
			
			
			
			System.out.println("  ");
			viewMovie();
			//System.out.println(st1);
			// st.executeUpdate(st1);
			
			System.out.println("  ");
			System.out.println("//////Please Enter Film ID of the Film You Wish To Reserve//////");
			int Film_ID = userInput.nextInt();
			
			
			String st1= "INSERT INTO Reserved (CustomerID, FilmID ) VALUES(" + Cus_ID + ",'"+ Film_ID + "')";
			
			
			
			//System.out.println(st1);
			 st.executeUpdate(st1);
	
			
	
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Customer Already Has Already Reserved The Movie Please Pick Another One");
		//e.printStackTrace();
			
			
		} 	
	
		
		
	}
public void showStatistics() {
	try {
		Statement st = connection.createStatement();
		ResultSet rs = st.executeQuery("SELECT * from Reserved,Film"); 
		System.out.println("//////Satistics Of the Most Popolar Movie//////");
		// This command reveals the movie that has been booked the most.   
		rs= st.executeQuery("select Title,Count(*) as NumReserve, Film.FilmID  FROM Reserved, Film Where Reserved.FilmID = Film.FilmID group by Title, Film.FilmID;" );
		while (rs.next()){
			int FilmID = rs.getInt("FilmID");
			String Title = rs.getString("Title");
			int Count = rs.getInt("NumReserve");
		
			System.out.print(FilmID + " ");
			System.out.print(Title + " ");
			System.out.print( Count +  " Time(s)" +" \n ");
			}
			while (rs.next()){
			//int CustomerID = rs.getInt("CustomerID");
			String FilmID = rs.getString("FilmID");
			int Count = rs.getInt("NumReserve");
			//System.out.print(CustomerID + " ");
			System.out.print(Count + " ");
			System.out.print(FilmID + " \n ");
		}	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Customer Has Already Reserved This Movie Kindly Pick Another One");
		//e.printStackTrace();	
		}
}
public void Movies()

{
	try {Statement st = connection.createStatement(); 							// create an SQL statement

	ResultSet rs = st.executeQuery("SELECT * from Film ");  // retrieve an SQL results set

	System.out.println("//////Movies that Start With The Letter D//////");
	rs=	st.executeQuery(" select * fROM Film\r\n" + " Where Title   LIKE 'd%'; ;  ");
	while (rs.next()){int FilmID = rs.getInt("FilmID");
			String Title = rs.getString("Title");
			String RentalPrice = rs.getString("RentalPrice");
			String Kind = rs.getString("Kind");
				System.out.print(FilmID + " ");
				System.out.print(Title + " ");
				System.out.print(RentalPrice + " ");
				System.out.print(Kind + " \n");
					}
			if (st != null) {
				st.close();		//close the SQL statement
				}

			if (rs != null){	//close the Result Set
				rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				}
}
public void ExpensiveMovie()
{
	try {
		Statement st = connection.createStatement(); 							// create an SQL statement
		ResultSet rs = st.executeQuery("SELECT * from Customer, Reserved,Film ");  // retrieve an SQL results set
		rs=	st.executeQuery("SELECT DISTINCT Name, Customer.CustomerID  FROM Film, Reserved,Customer Where Reserved.CustomerID = Customer.CustomerID AND Reserved.FilmID = Film.FilmID AND RentalPrice > 3 ;  ");
		System.out.println("//////All customers who reserved Movies//////");
		while (rs.next()){
			int CustomerID = rs.getInt("CustomerID");
			String Name = rs.getString("Name");
			System.out.print(Name + "\n");
		}
		while (rs.next()){
			}
		if (st != null) {
st.close();		//close the SQL statement
			}
		if (rs != null){	//close the Result Set
rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			}
	}

public void FreqCust()
{
	try {
		Statement st = connection.createStatement(); 							// create an SQL statement
		ResultSet rs = st.executeQuery("SELECT * from  Reserved");  // retrieve an SQL results set
		rs= st.executeQuery("select CustomerID, Count(*)as FrequentCustomer from Reserved group by CustomerID;" );
		System.out.println("/////////Most Frequent Customers////////");
		while (rs.next()){
			int CustomerID = rs.getInt("CustomerID");
			int Count = rs.getInt("FrequentCustomer");
			System.out.print(CustomerID + " ");
			System.out.print(Count+ "\n");
			}
		if (st != null) {
			st.close();		//close the SQL statement
		}
		if (rs != null){	//close the Result Set
			rs.close();
		}
			} catch (SQLException e) {
				e.printStackTrace();
		}
	}



	public static void main(String[] args) {
		System.out.println("Starting");
		String mysqlUsername = "s2980940";
		String mysqlPassword = "casallys";
		String mysqlDatabaseName = "s2980940";
		String sshUsername = "s2980940";
		String sshPassword = "casallys";
		String sshRemoteHost = "knuth.gcd.ie";
		int shhRemotePort = 22;                     
		int localPort = 3310;           	
		String mysqlHost="localhost"; 
		int remoteMySQLPort = 3306;
		
		jdbcConnect con = new jdbcConnect();
		
		con.openSSHTunnel(sshUsername, sshPassword, sshRemoteHost, shhRemotePort, mysqlHost, localPort, remoteMySQLPort);
		con.openConnection(mysqlHost, localPort, mysqlDatabaseName, mysqlUsername, mysqlPassword);
			
//		con.testConnection();
		
		Scanner CustInput= new Scanner (System.in);
		int response;
		do {
			
			System.out.println("Enter 1 to Register as a new Customer");
			System.out.println("Enter 2 to view Reservation");
			System.out.println("Enter 3 to make a reservation");
			System.out.println("Enter 4 to show Statistics");
			System.out.println("Enter 5 to Exit");
			int digit = CustInput.nextInt();
			switch (digit) {
			case 1:
				System.out.println("//////REGISTER NEW CUSTOMER//////");
				con.CustomerDetails();
				
				break;
					
			case 2: 
				System.out.println("//////VIEW RESERVATION//////");
				con.ViewReservations();
				
				
				break;
				
			case 3: 
				System.out.println("//////MAKE RESERVATION//////");
				con.MakeReservation();
		
				break;
				
			case 4: 
				System.out.println("//////SHOW STATISTICS//////");
				con.showStatistics();
				con.Movies();
				con.ExpensiveMovie();
				con.FreqCust();
				break;
			 
			case 5:
				System.out.println("Good bye");
				break;
				default:
					System.out.println("Selection not allowed"); 
			
			
			} 
			System.out.println("Enter X to re-select an option");
			response= CustInput.next().charAt(0);
		}
		while(response != 'z' && response != 'Z');
			System.out.println("Thank you for your selection");
			
		
		con.closeConnection();
		con.closeSshTunnel(localPort);
	}

}
