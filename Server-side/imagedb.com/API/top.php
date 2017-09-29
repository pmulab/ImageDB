 <?php
	require_once 'include/Config.php';

	if($_GET['place'])
	{
		// TEMP: Get 1-st image // TODO:get by place number
		// Create connection
		$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);

		//$place = strtolower($_GET['place']);
		$sql = "SELECT *
				FROM ImageStorage
				WHERE rating = (SELECT MAX(rating) FROM ImageStorage)				
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
	else
	{
		// Create connection
		$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);

		$place = strtolower($_GET['place']);
		$sql = "SELECT * 
				FROM ImageStorage
				WHERE rating > -1
				ORDER BY rating DESC
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