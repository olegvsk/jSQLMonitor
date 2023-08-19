select SPID            = S.session_id,
       Login           = S.login_name,
       Status          = isnull(R.status,S.status),
       Command         = R.command,
       DB              = isnull(db_name(nullif(S.database_id,0)),''),
       ProgName        = S.program_name,
       LoginTime       = S.login_time,
       LastTime        = isnull(S.last_request_end_time,S.last_request_start_time),
       CPU             = isnull(R.cpu_time,0)+S.cpu_time,
       IO              = isnull(R.reads+R.writes,0)+S.reads+S.writes,
       Mem             = S.memory_usage,
       Host            = S.host_name,
       NTLogin         = S.nt_user_name,
       ClientInterface = S.client_interface_name,
       NTDomain        = S.nt_domain,
       ElapsedTime     = isnull(R.total_elapsed_time,S.total_elapsed_time),
       Reads           = isnull(R.reads,0)+S.reads,
       Writes          = isnull(R.writes,0)+S.writes,
       LogicalReads    = isnull(R.logical_reads,S.logical_reads),
       IsolationLevel  = isnull(R.transaction_isolation_level,S.transaction_isolation_level),
       LockTimeout     = isnull(R.lock_timeout,S.lock_timeout),
       RowCnt          = isnull(R.row_count,S.row_count),
       OriginalLogin   = S.original_login_name,
       OpenTran        = isnull(R.open_transaction_count,S.open_transaction_count),
       WaitType        = R.wait_type,
       WaitTime        = R.wait_time,
       WaitResource    = R.wait_resource,
       NestedLevel     = R.nest_level,
       CLR             = R.executing_managed_code,
       NetTransport    = C.net_transport,
       Protocol        = C.protocol_type,
       AuthScheme      = C.auth_scheme,
       ClientAddress   = C.client_net_address,
       ClientPort      = C.client_tcp_port,
       ServerAddress   = C.local_net_address,
       ServerPort      = C.local_tcp_port,
       Statement       = substring(T.text,R.statement_start_offset/2+1,(case R.statement_end_offset when -1 then datalength(T.text) else R.statement_end_offset end)/2),
       Batch           = T.text,
       BlockedBy       = nullif(R.blocking_session_id,0),

-- Field "Color" sets the RGB color of the row and is not displayed in the table. Must be int.
       Color = cast(case when exists(select * 
                                     from sys.dm_exec_requests 
                                     where blocking_session_id=S.session_id) then 0xFF0000    -- W3C.red 
                         when isnull(R.blocking_session_id,0)>0              then 0xFFAAFF    -- some pink
                         when isnull(R.status,S.status)='running'            then 0x80FF80    -- light green
                         when isnull(R.status,S.status)='runnable'           then 0xDCFFDC    -- very light green
                         when isnull(R.status,S.status)='suspended'          then 0xDAA520    -- W3C.goldenrod
                         when isnull(R.status,S.status)='dormant'            then 0xFFFF00    -- W3C.yellow
                                                                             else 0xFFFFE0    -- W3C.lightyellow
                    end as int)
from sys.dm_exec_sessions S
left join sys.dm_exec_requests R on R.session_id=S.session_id
left join sys.dm_exec_connections C on C.session_id=S.session_id
outer apply sys.dm_exec_sql_text(R.sql_handle) T
where S.is_user_process=1    -- only user processes
order by 1
