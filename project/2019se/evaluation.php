<?php
//echo "4444";

$bookid = $_GET['bookid'];
$nowtime = $_GET['nowtime'];

$conn = new mysqli("47.102.114.254", "root", "Nanrunkai666!", "share_book", "3306");
// if ($conn->connect_error)
$str=$nowtime."%";
//echo json_encode($str);

$success=0;
$ans="";
    /*
    for ($j = 0; $j < 60; $j++) {
        $str=$nowtime;
        if ($j < 10) {
            $str.="0";
            $str.=(string)$j;
        } else {
            $str.=(string)$j;
        }
        $sql = "SELECT evaluate FROM `Book_User` WHERE book_id = '$bookid' AND get_time = '$str'";
        $query = $conn->query($sql);
        if ($success == 0) {
            while ($row = $query->fetch_assoc()) {
                //  $now->book_id = $row["book_id"];
                // $now->site_id = $row["site_id"];
                //  $now->user_id = $row["user_id"];
                //  $now->operate = $row["operate"];
                $success++;
                $ans=$row['evaluate'];
            }
        }
    } */

$sql = "SELECT evaluate FROM `Book_User` WHERE book_id = '$bookid' AND get_time LIKE '$str'";
$query = $conn->query($sql);
if ($success == 0) {
    while ($row = $query->fetch_assoc()) {
        //  $now->book_id = $row["book_id"];
        // $now->site_id = $row["site_id"];
        //  $now->user_id = $row["user_id"];
        //  $now->operate = $row["operate"];
        $success++;
        $ans=$row['evaluate'];
    }
}
    if ($success==1) {
        echo json_encode($ans);
    } else {
        echo "No evaluation yet!";
    }
//$sql = "SELECT evaluate FROM `Book_User` WHERE book_id = \"0000001\" ORDER BY time DESC";
//$query = $conn->query($sql);
$conn->close();
?>