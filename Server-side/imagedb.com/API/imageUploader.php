<?php
	require_once 'include/Config.php';	//include 'DatabaseConfig.php';

	// Create connection
	$conn = new mysqli(DB_HOST, DB_USER, DB_PASSWORD, DB_DATABASE);
	 
	 if($_SERVER['REQUEST_METHOD'] == 'POST')
	 {
		 $DefaultId = 0;
		 
		 $ImageData = $_POST['image_path'];	 
		 $ImageName = $_POST['image_name'];
		 $AutorUniqueUserId = $_POST['author_unique_user_id'];		 
		 $CreatedAt = $_POST['created_at'];
		 //$Rating = $_POST['rating'];
		 $GetOldIdSQL ="SELECT id FROM ImageStorage ORDER BY id ASC";
		 
		 $Query = mysqli_query($conn,$GetOldIdSQL);
		 
		 while($row = mysqli_fetch_array($Query))
		 {	 
			$DefaultId = $row['id'];
		 }
		 
		 $ImagePath = "images/$DefaultId.png";
		 $ImageToPutPath = "C:/myprogram/openserver/openserver/domains/imagedb/images/$DefaultId.png";
		 $ServerURL = "http://imagedb/$ImagePath";
		 $InsertSQL = "insert into ImageStorage (image_path,image_name,author_unique_user_id,created_at,rating) values ('$ServerURL','$ImageName','$AutorUniqueUserId',NOW(),'-1')";	

		 if(mysqli_query($conn, $InsertSQL))
		 {
			file_put_contents($ImageToPutPath, base64_decode($ImageData));
			echo "Your Image Has Been Uploaded.";
		 }	 
		mysqli_close($conn);
	 }
	 else
	 {
		echo "Not Uploaded";
	 }
?>