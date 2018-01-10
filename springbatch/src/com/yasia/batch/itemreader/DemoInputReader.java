package com.yasia.batch.itemreader;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

/**
 * 
 * @author LinYunZhi
 * @since 2018-01-07 22:16:49
 */
public class DemoInputReader implements ItemReader<String> {
	
	private Iterator<String> strList;

	public DemoInputReader(List<String> asList) {
		//可以将数据库连接信息用map传入，然后读取数据库数据返回
		strList = asList.iterator();
	}

	/**
	 * 读数据，只负责读取，如果需要處理，交由处理类
	 */
	@Override
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if(this.strList.hasNext()) {
			return this.strList.next();
		};
		return null;
	}

}
