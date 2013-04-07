namespace java fengfei.cruc.thrift
namespace go fengfei.cruc.thrift

exception CrucException {
  1:i32 code
  2: string description
}

 
 

service CrucService{
  
  #checking thrift connection validation
  string ping(1:string cmd) throws(1: CrucException ex)
  
  #for Dynamic Interface 
  binary call(  1: string namegroup,
 				2: string interfaceName,
  				3: i32 version,
  				4: list<string> params) throws(1: CrucException ex)
  
 
}

service PingService{
  
  #checking thrift connection validation
  string ping(1:string cmd) throws(1: CrucException ex)
  
  #for Dynamic Interface 
  binary call(  1: string namegroup,
 				2: string interfaceName,
  				3: i32 version,
  				4: list<string> params) throws(1: CrucException ex)
  
 
}
