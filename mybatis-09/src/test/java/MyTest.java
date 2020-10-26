import com.chen.mapper.UserMapper;
import com.chen.pojo.User;
import com.chen.util.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class MyTest {
    @Test
    public void getUserByID(){
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User userByID = mapper.getUserByID(1);
        System.out.println(userByID.toString());
        sqlSession.close();

        SqlSession sqlSession2 = MybatisUtils.getSqlSession();
        UserMapper mapper2 = sqlSession2.getMapper(UserMapper.class);
        User userByID2 = mapper2.getUserByID(1);
        System.out.println(userByID2.toString());
        sqlSession2.close();
    }
}
