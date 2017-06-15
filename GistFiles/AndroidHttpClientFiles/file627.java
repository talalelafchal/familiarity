interface ISimplxLogger{
  public static function log($level,$description);
}

class simplx__mygit_logger implements ISimplxLogger{
  public static function log($level,$description){
 
  }
}

interface IRestProxy{
  public function setServiceURI($uri);
  public function login($usn,$psw);
  public function logout();  
  public function get($uri,$data='');
  public function post($uri,$data='');
  public function patch($uri,$data='');
  public function put($uri,$data='');
  public function delete($uri,$data='');
}

class GitAPIClient implements IRestProxy{

  protected $username = '';
  protected $password = '';
  protected $serviceUri = 'https://api.github.com/';

  public function setServiceURI($uri){
    $this->serviceUri = $uri;
  }

  public function login($usn,$psw){
        
    if($usn && $psw){
      $this->username = $usn;
      $this->password = $psw;
    }else{
      // Exception in simplx_mygit.login(): Missing required params.
    	return false;
    }
  }

  public function logout(){
  
  }  
  
  public function get($uri,$data=''){
    return  $this->execute($uri,$data,'GET');
  }

  public function post($uri,$data=''){
    return $this->execute($uri,$data,'POST');
  }

  public function patch($uri,$data=''){
    return $this->execute($uri,$data,'POST');    
    // return self::execute($uri,$data,'PATCH');	  
  }

  public function put($uri,$data=''){
    return $this->execute($uri,$data,'PUT');  
  }
  
  public function delete($uri,$data=''){
    return $this->execute($uri,$data,'DELETE');  
  }
  
  private function execute($uri,$data = '',$method = 'GET'){

    if ($this->serviceUri != '' && $uri != '') {
      $ch = curl_init();
      
      // Use basic auth only if both username and password are provided
      if($this->username && $this->password){
      	curl_setopt($ch, CURLOPT_USERPWD, ($this->username.':'.$this->password));
      }
      
      curl_setopt($ch,CURLOPT_URL,$this->serviceUri.$uri);
      
      switch($method){
      	case 'POST':
	    curl_setopt($ch,CURLOPT_POST,1);
	    curl_setopt($ch,CURLOPT_POSTFIELDS,$data);
	    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);	

	    break;

	case 'PATCH':	  
	    curl_setopt($ch, CURLOPT_POSTFIELDS, $data);
	    curl_setopt($ch, CURLOPT_FOLLOWLOCATION, 1);
	    curl_setopt($ch, CURLOPT_HEADER, 0); 
	    curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'PATCH');
	  	
	    break;

	case 'DELETE':
	    
	    curl_setopt($ch, CURLOPT_POSTFIELDS, $data);
	    curl_setopt($ch, CURLOPT_FOLLOWLOCATION, 1);
	    curl_setopt($ch, CURLOPT_HEADER, 0); 
	    curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'DELETE');
	    
	    break;

	case 'PUT':
	/*	    
	    curl_setopt($ch, CURLOPT_VERBOSE, 1);
	    curl_setopt($ch, CURLOPT_PUT, 1);
	    curl_setopt($ch, CURLOPT_INFILE, $fp);
	    curl_setopt($ch, CURLOPT_INFILESIZE, filesize($localfile));	    
*/  
	    break;
	default:
	    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
	      
      }  

      $output = curl_exec($ch);
      curl_close($ch);  
      return $output;

    }else{
      // Exception in simplx_mygit.excecuteApiAction(): Missing required params.
      return false;
    }
  }
}


class simplx_mygit{
  protected $gistList = array();
  protected $IRestProxy;
  
  function __construct(&$apiProxy = null){  	
    if($apiProxy instanceof IRestProxy){
    	$this->IRestProxy = $apiProxy;
      	return self;
    }else{
	return false;
    }
    
  }  
  
  public function setLoggingService($logger){
    if($logger instanceof ISimplxLogger){
       	
    }
  }
  
  public function log($level=0,$description=''){
  	$loggingService->log($level,$description);
  }
  
  public function login($usn,$psw){
        
    if($usn && $psw){
	$this->IRestProxy->login($usn,$psw);
    }else{
      // Exception in simplx_mygit.login(): Missing required params.
    	return false;
    }
  }

  public function logout(){
  
  }  

  
  public function getStarred(){
  
  }
  
  public function getGists($userid = null){    
    
    $resultSet = array();
    
    if(!$userid){

    }else{
    
    }
    
    $result =  $this->IRestProxy->get('users/'.$userid.'/gists','');
 
    if($result){    
      $resultSet = json_decode($result,true);

      if(is_array($resultSet)){
	foreach ($resultSet as  $item) {	  
	  $resultSet[] = new simplx_gist($item);
	}
	
	return $resultSet;
	
      }else{
	// Exception in simplx_mygit.getGists(): Unable to decode api.github.com response.
      	return false;
      }
    }else{
      // Exception in simplx_mygit.getGists(): Empty response from api.github.
      return false;
    }
  }

  public function getGist($id){
    
    if(isset($id)){
      $result =  $this->IRestProxy->get('gists/'.$id);
      if($result){
	$result = json_decode($result,true);
	if(is_array($result)){	  
	  return new simplx_gist($result);
	}else{
	  //Exception in simplx_mygit.getGist(): Unable to decode and init Gist object.
	  return false;
	}
	
      }else{
	return false;
      }
    }
  }
  
  public function searchGists($keywords){
  
  }
  
  public function addGist(&$gist){
    
    $gist_data = '';

    if(isset($gist)){
      
      $gist_data = json_encode($gist);
      $result =  $this->IRestProxy->post('gists',$gist_data);
      if($result){
	$result = json_decode($result,true);
	if(is_array($result)){	  
	  $gist->reinit($result);
	  return true;
	}else{
	  //Exception in simplx_mygit.createGist(): Unable to decode and init Gist object.
	  return false;
	}
	
      }else{
	return false;
      }
    }
  }

  public function saveGist(&$gist){
    
    $gist_data = '';

    if(isset($gist)){

      if($gist->id != ''){

	$gist_data = json_encode($gist);
	$result =  $this->IRestProxy->patch(('gists/'.$gist->id),$gist_data);

	if($result){
	  $result = json_decode($result,true);

	  if(is_array($result)){	  
	    $gist->reinit($result);
	    return true;
	  }else{
	    // Exception in simplx_mygit.createGist(): Unable to decode and init Gist object.
	    return false;
	  }
	
	}else{
	  return false;
	}
  
      }else{
	if($this->addGist($gist)){
		return true;
	}else{
		return false;
	}
      }
    }
    
  }
  
  public function deleteGist(&$gist){
    
    $gist_data = '';

    if(isset($gist)){

      if($gist->id != ''){

	$gist_data = json_encode($gist);
	$result =  $this->IRestProxy->delete(('gists/'.$gist->id),$gist_data);

	if($result){
	  return true;	
	}else{
	  return false;
	}
  
      }else{
	return false;
      }
    }
  }
  

}

class simplx_git_base{

 

}

class simplx_git_user extends simplx_git_base{

  public $login;
  public $id;
  public $gravatar_url;
  public $url; 

  function __construct($state = null) {
    
    if($state){
      if(is_array($state)){
	$this->init($state);  	
      }else{
	$state = json_decode($state,true);
	if(is_array($state)){
	  $this->init($state);  
	  return $this;
	}else{
	  // Exception in simplx_git_base.__construct(): Unable to decode object state.
	  return false;
	}
      }
    }
 } 
  
  function init($stateArray){
    $this->login = $stateArray['login'];
    $this->id = $stateArray['id'];
    $this->gravatar_url = $stateArray['gravatar_url'];
    $this->url = $stateArray['url'];
  }
}

class simplx_gist extends simplx_git_base{

  public $url = '';
  public $id = '';
  public $description = '';
  public $public = true;
  public $user = null;
  public $files = array();  
  public $comments = 0;  
  public $git_pull_url = '';
  public $git_push_url = '';
  public $created_at = '';
  public $forks = array(
    /*[
    {
      "user": {
        $login = "octocat";
        $id": 1,
        $gravatar_url = "https://github.com/images/error/octocat_happy.gif";
        $url = "https://api.github.com/users/octocat"
      },
      $url = "https://api.github.com/gists/5";
      $created_at = "2011-04-14T16:00:49Z"
    }*/
  );

  public $history = array(); 
  
  function __construct($state = null) {
    
    if($state){
      if(is_array($state)){
	$this->init($state);  	
      }else{
	$state = json_decode($state,true);
	if(is_array($state)){
	  $this->init($state);  
	  return $this;
	}else{
	  // Exception in simplx_mygit.__construct(): Unable to decode object state.
	  return false;
	}
      }
    }
 }     
  
  public function addFile(&$file){
    if(isset($file)){
      if(is_array($file)){
      	// Got an array
	// Check for duplicate
	//if(array_key_exists($this->files,$file['filename'])){
	  // Exception in simplx_mygit.addFile(): File name exists.
	//return false;
	  
	//}else{
	  $this->files[$file['filename']] = new simplx_gist_file($file);	
	//}


      }elseif(is_object($file)){
	// Got an object
	// Check for duplicate
	//if(array_key_exists($this->files,$file->filename)){
	  // Exception in simplx_mygit.addFile(): File name exists.
	// return false;	  	
	  
	  //}else{
	 $this->files[$file->filename] = $file;
	  
	  //}


      }
    }else{
	// Exception in simplx_mygit.deleteFile(): Required method parameter missing.
    	return false;
    }
  }

  public function saveFile(&$file){
    if(isset($file)){
      if(is_array($file)){
      	// Got an array
	$this->files[$file['filename']] = $file;
      }elseif(is_object($file)){
	// Got an object
	$this->files[$file->filename] = $file;
      }
    }else{
	// Exception in simplx_mygit.saveFile((): Required method parameter missing.
    	return false;
    }
  }  
  
  public function deleteFile($file){
    if(isset($file)){
      if(is_array($file)){
      	// Got an array
	unset($this->files[$file['filename']]);
      }elseif(is_object($file)){
	// Got an object
	unset($this->files[$file->filename]);
      }else{
	// Guess its a filename string
	unset($this->files[$file]);
      }
    }else{
	// Exception in simplx_mygit.deleteFile(): Required method parameter missing.
    	return false;
    }
  }

  public function getFile($filename){
    $gistFile;
    
    if(isset($filename)){
      if(array_key_exists($filename,$this->files)){
	$gistFile = $this->files[$filename];

	// See if the returned file was stored as object or array.
	if(!is_object($gistFile)){	  	
	  $gistFile = new simplx_gist_file($gistFile);	  
	}
	return $gistFile;      
	
      }else{
      	return false;
      }
    }else{
    	return false;
    }
  }

  
  public function reinit($state){
    if($state){
    	$this->init($state);
      	return $this;
    }else{
    	return false;
    }
  }
  
  private function init($stateArray){
        

    if(isset($stateArray)){

      if(is_array($stateArray)){
		  
	$this->url = $stateArray['url'];
	$this->id = $stateArray['id'];
	$this->description = $stateArray['description'];
	$this->public = (boolean)$stateArray['public'];
	$this->user = new simplx_git_user($stateArray['user']);
	
	$this->git_pull_url = $stateArray['git_pull_url'];
	$this->git_push_url = $stateArray['git_push_url'];
	$this->created_at = $stateArray['created_at'];

	$this->files = $stateArray['files'];	
	$this->comments = $stateArray['comments'];

	$this->forks = $stateArray['forks'];
	$this->history = $stateArray['comments'];
	      
      }else{
	// Exception in simplx_gist.init(): Gist object state was not of type Array().
      	return false;
      }

    }else{
	// Exception in simplx_gist.init(): Required params missing in method call.
      return false;
    }
  }
  
  private function initFiles(){
      foreach ($this->files as $key => &$value){
	//$value = new 
      }
  }

  private function initHistory(){
      foreach ($this->history as $key => &$value){

      }  
  }

  private function initComments(){
      foreach ($this->comments as $key => &$value){

      }  
  }

  private function initForks(){
      foreach ($this->forks as $key => &$value){

      }  
  }

  public function getFiles(){

    // On-demand init the file collection 
    foreach ($this->files as $key => &$value){
	$value = new simplx_gist_file($value);
    }
    
    return $this->files;
  }

  public function getHistory(){
  	return $this->history;
  }

  public function getForks(){
    return $this->forks;  
  }

}


class simplx_gist_changeStatus extends simplx_git_base{
  public $deletions = 0;
  public $additions = 0;  
  public $total = 0;

  /*  
      $change_status": {
        $deletions": 0,
        $additions": 180,
        $total": 180
      }
*/  
  function __construct($state = null) {
    
    if($state){
      if(is_array($state)){
	$this->init($state);  	
      }else{
	$state = json_decode($state,true);
	if(is_array($state)){
	  $this->init($state);  
	  return $this;
	}else{
	  // Exception in simplx_git_base.__construct(): Unable to decode object state.
	  return false;
	}
      }
    }
 } 
  
  function init($stateArray){
    $this->deletions = $stateArray['deletions'];
    $this->additions = $stateArray['additions'];
    $this->total = $stateArray['total'];
  }  

}


class simplx_gist_history extends simplx_git_base{
  public $url = '';
  public $version = '';  
  public $user = null;
  public $change_status = '';
  public $comitted_at = '';  

  /*  
    {
      $url = "https://api.github.com/gists/1/57a7f021a713b1c5a6a199b54cc514735d2d462f";
      $version = "57a7f021a713b1c5a6a199b54cc514735d2d462f";
      $user": {
        $login = "octocat";
        $id": 1,
        $gravatar_url = "https://github.com/images/error/octocat_happy.gif";
        $url = "https://api.github.com/users/octocat"
      },
      $change_status": {
        $deletions": 0,
        $additions": 180,
        $total": 180
      },
      $committed_at = "2010-04-14T02:15:15Z"
    }
*/  
  
  function __construct($state = null) {
    
    if($state){
      if(is_array($state)){
	$this->init($state);  	
      }else{
	$state = json_decode($state,true);
	if(is_array($state)){
	  $this->init($state);  
	  return $this;
	}else{
	  // Exception in simplx_git_base.__construct(): Unable to decode object state.
	  return false;
	}
      }
    }
 } 
  
  function init($stateArray){
    $this->url = $stateArray['url'];
    $this->version = $stateArray['version'];
    $this->user = new simplx_git_user($stateArray['user']);
    $this->change_status = new simplx_gist_changeStatus($stateArray['change_status']);
    $this->committed_at = $stateArray['committed_at'];    
  }  

}

/*

*/

class simplx_gist_file extends simplx_git_base{
  public $id = '';
  public $filename = '';  
  public $size = 0;
  public $raw_url = '';
  public $content = '';  

  /*
{
      "size": 932,
      "filename": "ring.erl",
      "raw_url": "https://gist.github.com/raw/365370/8c4d2d43d178df44f4c03a7f2ac0ff512853564e/ring.erl",
      "content": "contents of gist"
    }
  */  

  function __construct($state = null) {
    
    if($state){
      if(is_array($state)){
	$this->init($state);  	
      }else{
	$state = json_decode($state,true);
	if(is_array($state)){
	  $this->init($state);  
	  return $this;
	}else{
	  // Exception in simplx_git_base.__construct(): Unable to decode object state.
	  return false;
	}
      }
    }
 } 
  
  function init($stateArray){
    $this->filename = $stateArray['filename'];
    $this->id = $stateArray['id'];
    $this->raw_url = $stateArray['raw_url'];
    $this->content = $stateArray['content'];    
    $this->size = mb_strlen($this->content,'latin1');    
  }  

}
