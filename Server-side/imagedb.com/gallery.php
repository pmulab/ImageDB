<!DOCTYPE html>
<html>
<head>
	<meta name="viewport" content="width=425px, user-scalable=no">

	<link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css">
	<link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap-theme.min.css">
	<link href="//netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css" rel="stylesheet">
	<script src="//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script>

	<title>ImageDB - Gallery</title>
</head> 
<body style="margin-left:20px;width:300px;zoom:125%;">
	<h3>ImageDB - Gallery</h3>
	<a href='.' style='width:150px;' class='btn btn-info'>Go Home</a>
	<br><br>

	 <?php
		require_once 'API/include/Config.php';

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

			while($data = mysqli_fetch_array($result)){
				echo "<div class='well well-sm' style='padding-top:4px;padding-bottom:8px; margin-bottom:8px; overflow:hidden;'>";
				echo "<div style='font-size:10px;float:right;'>".time_elapsed_string($data['created_at'])."</div>";
				echo "<table>";
				echo "<tr>";
				echo "<td valign=top style='padding-top:4px;'>";
				echo "</td>";;
				echo "<td style='padding-left:5px;word-wrap: break-word;' valign=top>";
				//echo "<a style='font-size:12px;' href='./".$data['username']."'>@".$data['username']."</a>";
				//	$new_tweet = preg_replace('/@(\\w+)/','<a href=./$1>$0</a>',$data['data']);
				//	$new_tweet = preg_replace('/#(\\w+)/','<a href=./hashtag/$1>$0</a>',$new_tweet);
				//	echo "<div style='font-size:11px; margin-top:-3px;'>".$new_tweet."</div>";
				$img_url = $data['image_path'];		
				echo "<div style='font-size:11px; margin-top:-3px;'> Hi-Res : <a href=$img_url>$img_url</a> </div>";
				echo "<div style='font-size:11px; margin-top:-3px;'> Name : ".$data['image_name']."</div>";
				echo "<div style='font-size:11px; margin-top:-3px;'> Rating :".$data['rating']."</div>";
				echo "<img class='card-img-top' data-src='' alt='Image placeholder' style='width:200px'; display: block;' src='$img_url' data-holder-rendered='true'>";
				echo "</td>";
				echo "</tr>";
				echo "</table>";
				echo "</div>";
			}
			$conn->close();
		}
		
		function time_elapsed_string($datetime, $full = false) {
			$now = new DateTime;
			$ago = new DateTime($datetime);
			$diff = $now->diff($ago);

			$diff->w = floor($diff->d / 7);
			$diff->d -= $diff->w * 7;

			$string = array(
				'y' => 'year',
				'm' => 'month',
				'w' => 'week',
				'd' => 'day',
				'h' => 'hour',
				'i' => 'minute',
				's' => 'second',
			);
			foreach ($string as $k => &$v) {
				if ($diff->$k) {
					$v = $diff->$k . ' ' . $v . ($diff->$k > 1 ? 's' : '');
				} else {
					unset($string[$k]);
				}
			}

			if (!$full) $string = array_slice($string, 0, 1);
			return $string ? implode(', ', $string) . ' ago' : 'just now';
		}
	?>
	
	
	<br>
	<div class="jumbotron" style="padding:3px;">
		<div class="container">
		<h5>ImageDB &copy; 2017</h5>
		</div>
	</div>
</body>
</html>