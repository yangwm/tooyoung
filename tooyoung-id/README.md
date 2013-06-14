
分布式uuid发号基础模块

版本：tooyoung-uuid-1.0

一，简介：
分布式uuid发号基础模块，俗称“uuid发号器”。
是为了解决分布式数据库部署过程中，数据唯一标识冲突的问题而提出的解决方案。

采用了高性能的服务器框架，源于Redis系统的网络IO部分代码，具有业界公认的高性能和高稳定性。
实现了Memcache协议中的GET指令，用于客户端请求UUID，具有很好的通用性，应用改造成本很低。
采用了精密的算法，保证了UUID在“秒”级别能够保持顺序递增的特性。
具有良好的可扩展性，最多可以部署8个实例(4个idc*2个ha)。
每个实例保持独立，实例间无任何通信过程，可以方便的进行七层、四层、客户端等形式的负载均衡。
在单CPU虚拟机上进行简单测试，单实例每秒可以响应10000次以上的请求。
理论上每个机房部署两台服务器即可满足整个机房所有应用所有UUID的需求。


二，原理：
uuid为52bit的整型: all(52bit)=time(28bit)+seq(15bit)+biz(6bit)+idc(2bit)+ha(1bit) 

time:Linux时间戳-1230739200
seq：自增序列号（最大支持每秒3.2w）
biz：业务号（0~63）
idc：idc实例号（0~3）
ha：高可用号（0~1）

三，服务端使用说明：
可执行程序：bin/tooyoung-uuid-server
参数选项：
Tooyoung uuid server version 1.0
-p <num>      TCP port number to listen on (default: 5001)
-l <ip_addr>  interface to listen on (default: INADDR_ANY, all addresses)
-d            run as a daemon
-c <num>      max simultaneous connections (default: 1024)
-v            verbose (print errors/warnings while in event loop)
-i <num>      the idc id, must be a number between 0 and 3
-a <num>      the ha id, must be a number 0 or 1 

必须指定的参数：
-p 端口号 
-d 以daemon在后台运行 
-i 系统全局idc唯一的标识，必须在0-3之间，绝对不能重复。
-a 系统全局ha唯一的标识，必须在0-1之间，绝对不能重复。

运行示例：
tooyoung-uuid-server -p 5001 -d -i 0 -a 0
建议将多个实例的启动命令行写为脚本程序，防止发生错误。

五，客户端使用说明：
使用Memcache客户端直接连tooyoung-uuid-server，调用get(业务号)即可获得uuid。
业务号：0~3；非0~3时，返回错误。

特殊说明：
1，key支持忽略头尾空格，但是不支持忽略和其他特殊字符；
例如：“get  1uuid321 ”与“get 1”等效；
2，非数字的key，进行错误处理
例如：“get key”
3，数字<0或>63，进行错误处理
例如：“get 64”
4，数字前的000，对数字大小不产生影响
例如：“get  000001”与“get 1”等效
5，错误处理和以前uuid错误处理一样，切断连接。

六，测试方法：
使用任何支持文本协议的Memcache客户端或者telnet均可测试。
请求key为biz+uuid+randomId。

示例：
wumings-MacBook-Pro:tooyoung-uuid yangwm$ telnet 192.168.17.211 5001
Trying 192.168.17.211...
Connected to 192.168.17.211.
Escape character is '^]'.
get uuid
VALUE uuid 0 16
2335846585860608
END
get 3uuid321
VALUE uuid 0 16
2335846837519384
END
get 31uuid321
VALUE uuid 0 16
2335846988515064
END
