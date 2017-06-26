package net.shopxx.test.base;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;

public abstract class DbUnitTest extends BaseTest {

	
    @Resource 
    protected DataSource dataSource; 
	protected IDatabaseTester databaseTester;
	
	
	@Before
	public void setup() throws Exception {
		super.setup();
		this.databaseTester = new DataSourceDatabaseTester(this.dataSource); 
		List<String> list = getTables();
		for (String table : list) {
			InputStream input = DbUnitTest.class.getClass().getResourceAsStream("/shopxx/db/xml/"+table.toLowerCase()+".xml");
			 if(input== null){
				 continue;
			 }
			IDataSet dataSet = new FlatXmlDataSetBuilder().build(input);
			DatabaseOperation.DELETE_ALL.execute(getIConnection(), dataSet);
			DatabaseOperation.INSERT.execute(getIConnection(), dataSet);
			input.close();
		}
		databaseTester.setSetUpOperation(DatabaseOperation.NONE);
		databaseTester.setTearDownOperation(DatabaseOperation.NONE);
		databaseTester.onSetup();
	}
	
	public IDataSet getDataSetFromXml(String file) throws DataSetException{
		 InputStream input = DbUnitTest.class.getClass().getResourceAsStream(file);
		 if(input== null){
			 return null;
		 }
		 IDataSet dataSetExcept = new FlatXmlDataSetBuilder().build(input);
         return dataSetExcept;
	}
	
	public List<String> getTables() throws IOException{
		  InputStream input = DbUnitTest.class.getClass().getResourceAsStream("/shopxx/db/data.sql");
		  BufferedReader reader=new BufferedReader(new InputStreamReader(input,"UTF-8"));
		  String line=null;
		  String createTable="CREATE TABLE ";
		  List<String> list=new ArrayList<String>(200);
		  while((line=reader.readLine())!=null){   
				if(line.startsWith(createTable)){
				 int lastIndex = line.lastIndexOf('(');
				 if(lastIndex==-1)continue;
				 String table=line.substring(createTable.length(),lastIndex).trim();
				 if(table.startsWith("\"")&&table.endsWith("\"")){
					 table=table.substring(1,table.length()-1);
				 }
				 if(table.startsWith("`")&&table.endsWith("`")){
					 table=table.substring(1,table.length()-1);
				 }
				 list.add(table);
				}
		  }
		   return list;  

	}
	
	
	
	protected IDatabaseConnection getIConnection() throws Exception {
		IDatabaseConnection con = databaseTester.getConnection();
		DatabaseConfig config = con.getConfig();

		config.setProperty(DatabaseConfig.PROPERTY_BATCH_SIZE, new Integer(97));
		config.setProperty(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, true);
		config.setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true);

		return con;
	}
	@After
	public void tearDown() throws Exception {
		databaseTester.onTearDown();
	}

	
}
