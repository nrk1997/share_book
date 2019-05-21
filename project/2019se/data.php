<?php
//测试php是否可以拿到数据库中的数据
//echo "44444";

//做个路由 action为url中的参数

$action = $_GET['action'];
$key = $_GET['key'];
$sql;

switch($key) {
    case 'book_id':
        $sql = "SELECT book_id FROM `History` WHERE book_id = '$action' ORDER BY time DESC";
        break;
    case 'user_id':
        $sql = "SELECT user_id FROM `History` WHERE book_id = '$action' ORDER BY time DESC";
        break;
    case 'site_id':
        $sql = "SELECT site_id FROM `History` WHERE book_id = '$action' ORDER BY time DESC";
        break;
    case 'time':
        $sql = "SELECT time FROM `History` WHERE book_id = '$action' ORDER BY time DESC";
        break;
    case 'operate':
        $sql = "SELECT operate FROM `History` WHERE book_id = '$action' ORDER BY time DESC";
        break;
}

//查询方法
/**
 *
 */
//function book_id(){
    //测试 运行crud.html时是否可以获取到 下面这个字符串


    //查询表
   // $sql = "SELECT * FROM `t_users`";
   // $query = query_sql($sql);
   // while($row = $query->fetch_assoc()){
    //    $data[] = $row;
   // }
   $conn = new mysqli("47.102.114.254", "root", "Nanrunkai666!", "share_book", "3306");
   // if ($conn->connect_error)

    //$sql = "SELECT book_id FROM `History` WHERE book_id = \"0000001\" ORDER BY time DESC";
    $query = $conn->query($sql);
    $arr = array();
    $i = 0;
    while ($row = $query->fetch_assoc()) {
      //  $now->book_id = $row["book_id"];
       // $now->site_id = $row["site_id"];
      //  $now->user_id = $row["user_id"];
      //  $now->operate = $row["operate"];
          if ($i < 16) {
              $arr[] = $row[$key];
              $i++;
          }
      }
    echo json_encode(array($arr));
    $conn->close();



























   // $now = $key;
   // echo $now;
   /* $json = json_encode(array(
        "resultCode"=>200,
        "message"=>"查询成功！",
        "data"=>"qt"
    ),JSON_UNESCAPED_UNICODE);
   echo($arr);*/
   //
 //   echo $action;
   //
   // echo "46545465465456465";
    /*
    //转换成字符串JSON
    echo($json);*/


/**查询服务器中的数据
 * 1、连接数据库,参数分别为 服务器地址 / 用户名 / 密码 / 数据库名称
 * 2、返回一个包含参数列表的数组
 * 3、遍历$sqls这个数组，并把返回的值赋值给 $s
 * 4、执行一条mysql的查询语句
 * 5、关闭数据库
 * 6、返回执行后的数据
 */
function query_sql(){
    $mysqli = new mysqli("127.0.0.1", "root", "root", "crud");

    $sqls = func_get_args();
    foreach($sqls as $s){
        $query = $mysqli->query($s);
    }
    $mysqli->close();
    return $query;
}
?>