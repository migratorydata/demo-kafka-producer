using System;
using System.Collections.Generic;
using com.migratorydata.client;
using System.Threading;
namespace example
{
    class Program
    {
        static void Main(string[] args)
        {
            MigratoryDataClient client = new MigratoryDataClient();
            client.SetLogListener(new LogList(), MigratoryDataLogLevel.DEBUG);
            client.SetListener(new Listener());

            //client.SetEncryption(true);
            client.SetEntitlementToken("some-token");
            client.SetServers(new string[] { "127.0.0.1:8800" });

            List<string> subjects = new List<string>();
            subjects.Add("/live/data");
            client.Subscribe(subjects);

            client.Connect();

            while (true)
            {
                Thread.Sleep(3000);
            }
        }
        class Listener : MigratoryDataListener
        {
            public void OnMessage(MigratoryDataMessage message)
            {
                System.Console.WriteLine(message.ToString());
            }
            public void OnStatus(string status, string info)
            {
                System.Console.WriteLine(status + " " + info);
            }
        }
        class LogList : MigratoryDataLogListener
        {
            public void OnLog(string log, MigratoryDataLogLevel level)
            {
                string msg = string.Format("[{0:G}] [{1}] {2}", DateTime.Now, level, log);
                Console.WriteLine(msg);
            }
        }
    }
}

