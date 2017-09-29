 <?php
	require_once 'include/Config.php';

	if($_GET['userID'])
	{
		// Create connection
		$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
		
		$userid = strtolower($_GET['userID']);
		$sql = "SELECT * 
				FROM ImageStorage
				WHERE author_unique_user_id='$userid'
				";
		$result = $conn->query($sql);

		if ($result->num_rows > 0) 
		{
			 while($row[] = $result->fetch_assoc()) 
			 {		 
				$tem = $row;		 
				$json = json_encode($tem);	 	
			}	 
		}
		else 
		{
			echo "No Results Found.";
		}
		echo $json;
		$conn->close();
	}
?>