package com.maplestone.labstudio;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import org.junit.platform.commons.util.StringUtils;

import java.io.File;
import java.util.HashMap;

public class GeneratorFileTest {

    private static final String SRC = "N:\\2020work\\labstudio\\src\\main\\java\\com\\maplestone\\labstudio";
    private static final String ENTITY_PATH = File.separator + "entity";
    private static final String MAPPER_PATH =  File.separator +"mapper";
    private static final String SERVICE_PATH =  File.separator +"service";
    private static final String DAO_PATH =  File.separator +"dao";
    private static final String ROUTE_BASE_PATH = File.separator + "route";
    private static final String ROUTE_API = "api";
    private static final int MODE = 1; //1.Mybatis 2.Hibernate
    private static final String AUTHOR = "HeYongXian";
    private static final HashMap<String, String> entityMap = new HashMap<>();


    public static void main(String[] args) {
        getFile(SRC + ENTITY_PATH);
        outPutFile(MODE);
        System.out.println("Generated Done!");
    }

    private static void getFile(String path){
        File file = new File(path);
        File[] array = file.listFiles();
        if (null == array){
            return;
        }
        for (File value : array) {
            if (value.isFile())//如果是文件
            {
                String name = value.getName();
                if (!("BaseEntity.java".equals(name) ||
                        "IdEntity.java".equals(name) ||
                        "UIDEntity.java".equals(name) ||
                        "UUIDEntity.java".equals(name)
                )) {
                    String filePath = value.getPath();
                    filePath = filePath.replace(SRC + ENTITY_PATH, "");
                    filePath = filePath.replace(name, "");
                    String filePack = filePath.replace(File.separator, "");
                    String s = entityMap.get(name);
                    if (StringUtils.isNotBlank(s)) {
                        System.out.println("文件名重复：" + name + "  , 路径：" + filePath);
                    }
                    entityMap.put(name, filePack);
                }
            } else if (value.isDirectory())//如果是文件夹
            {
                getFile(value.getPath());
            }
        }
    }

    /**
     * 生成mapper 或者Dao
     * @param mode 1 mybatis 2 hibernate
     */
    public static void outPutFile(int mode){
        for (String name : entityMap.keySet()) {
            String pack = entityMap.get(name);
//            System.out.println(pack+ "." + name);
            if (1 == mode){
                outMapperFile(name, pack);
                outMybatisServiceFile(name, pack);
                outMybatisRouteFile(name, pack);
            }
            if (2 == mode){
                outDaoFile(name, pack);
                outHibernateServiceFile(name, pack);
                outHibernateRouteFile(name, pack);
            }
        }
    }

    public static void outMapperFile(String fileName, String pack){
        String filePath = SRC + MAPPER_PATH + File.separator + pack + File.separator;
        FileUtil.mkdir(filePath);

        String name = fileName.replace(".java", "");
        String mapperName = name + "Mapper.java";
        File file = new File(filePath + mapperName);
        if(!file.exists()){
            String content = "package com.maplestone.labstudio.mapper."+pack+";\n" +
                    "\n" +
                    "import com.baomidou.mybatisplus.core.mapper.BaseMapper;\n" +
                    "import com.maplestone.labstudio.entity."+pack+"."+name+";\n" +
                    "import org.apache.ibatis.annotations.Mapper;\n" +
                    getComments()+
                    "@Mapper\n" +
                    "public interface "+name+"Mapper extends BaseMapper<"+name+"> {\n" +
                    "}\n";
            FileUtil.writeBytes(content.getBytes(), filePath + mapperName);
        }

        String xmlName = name + "Mapper.xml";
        File xmlFile = new File(filePath + xmlName);
        if(!xmlFile.exists()){
            String content = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                    "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n" +
                    "<mapper namespace=\"com.maplestone.labstudio.mapper."+pack+"."+name+"Mapper\">\n" +
                    "\n" +
                    "\n" +
                    "</mapper>\n";
            FileUtil.writeBytes(content.getBytes(), filePath + xmlName);

        }
    }

    public static void outDaoFile(String fileName, String pack){
        String filePath = SRC + DAO_PATH + File.separator + pack + File.separator;
        FileUtil.mkdir(filePath);
        String name = fileName.replace(".java", "");
        String daoName = name + "Dao.java";

        File file = new File(filePath + daoName);
        if(!file.exists()){
            String content = "package com.maplestone.labstudio.dao."+pack+";\n" +
                    "\n" +
                    "import com.maplestone.labstudio.dao.BaseRepo;\n" +
                    "import com.maplestone.labstudio.entity."+pack+"."+name+";\n" +
                    "import org.springframework.stereotype.Repository;\n"+
                    "\n" +
                    "import java.util.List;"+
                    getComments()+
                    "@Repository\n" +
                    "public interface "+name+"Dao extends BaseRepo<"+name+", String> {\n" +
                    "    \n" +
                    "    void deleteAllByIdIn(List<String> ids);\n"+
                    "    \n" +
                    "}\n";
            FileUtil.writeBytes(content.getBytes(), filePath + daoName);
        }
    }

    public static void outMybatisServiceFile(String fileName, String pack){
        String filePath = SRC + SERVICE_PATH + File.separator + pack + File.separator;
        String name = fileName.replace(".java", "");
        FileUtil.mkdir(filePath);
        String serviceName = name + "Service.java";
        File file = new File(filePath + serviceName);
        if(!file.exists()){
            String content = "package com.maplestone.labstudio.service."+pack+";\n" +
                    "\n" +
                    "import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;\n" +
                    "import com.maplestone.labstudio.dto.PageDTO;\n" +
                    "import com.maplestone.labstudio.entity."+pack+"."+name+";\n" +
                    "import com.maplestone.labstudio.mapper."+pack+"."+name+"Mapper;\n" +
                    "import com.maplestone.labstudio.service.BaseIService;\n" +
                    "import org.springframework.stereotype.Service;\n" +
                    getComments()+
                    "@Service\n" +
                    "public class "+name+"Service extends BaseIService<"+name+"Mapper, "+name+">{\n" +
                    "\n" +
                    "    public Object getPage(PageDTO pageDTO) {\n" +
                    "        LambdaQueryWrapper<"+name+"> query = new LambdaQueryWrapper<>();\n" +
                    "        if (pageDTO.isList()){\n" +
                    "            return list(query);\n" +
                    "        }else{\n" +
                    "            return page(pageDTO.get(), query);\n" +
                    "        }\n" +
                    "    }\n" +
                    "    \n" +
                    "}\n";
            FileUtil.writeBytes(content.getBytes(), filePath + serviceName);
        }
    }

    public static void outHibernateServiceFile(String fileName, String pack){
        String filePath = SRC + SERVICE_PATH + File.separator + pack + File.separator;
        FileUtil.mkdir(filePath);
        String name = fileName.replace(".java", "");
        String serviceName = name + "Service.java";
        String lowerCamelName = toLowerCamel(name);
        File file = new File(filePath + serviceName);
        if(!file.exists()){
            String content = "package com.maplestone.labstudio.service."+pack+";\n" +
                    "\n" +
                    "import cn.hutool.core.collection.CollectionUtil;\n" +
                    "import com.maplestone.labstudio.common.utils.PageableUtils;\n" +
                    "import com.maplestone.labstudio.dao."+pack+"."+name+"Dao;\n" +
                    "import com.maplestone.labstudio.dto.PageDTO;\n" +
                    "import com.maplestone.labstudio.entity."+pack+"."+name+";\n" +
                    "import com.maplestone.labstudio.service.BaseService;\n" +
                    "import org.springframework.beans.factory.annotation.Autowired;\n" +
                    "import org.springframework.stereotype.Service;\n" +
                    "\n" +
                    "import java.util.List;\n" +
                    "import java.util.Optional;\n" +
                    getComments()+
                    "@Service\n" +
                    "public class "+name+"Service extends BaseService<"+name+", String> {\n" +
                    "\n" +
                    "    @Autowired\n" +
                    "    "+name+"Dao "+lowerCamelName+"Dao;\n" +
                    "\n" +
                    "    public Object getPage(PageDTO pageDTO) {\n" +
                    "        if (pageDTO.isList()){\n" +
                    "            return findAll();\n" +
                    "        }else{\n" +
                    "            return findAll(PageableUtils.pageable(pageDTO.getPage(), pageDTO.getSize()));\n" +
                    "        }\n" +
                    "    }\n" +
                    "\n" +
                    "    public void removeById(String id) {\n" +
                    "        Optional<"+name+"> "+lowerCamelName+"Optional = findById(id);\n" +
                    "        "+lowerCamelName+"Optional.ifPresent(this::delete);\n" +
                    "    }\n" +
                    "\n" +
                    "    public void removeByIds(List<String> ids) {\n" +
                    "        if (CollectionUtil.isNotEmpty(ids)){\n" +
                    "            "+lowerCamelName+"Dao.deleteAllByIdIn(ids);\n" +
                    "        }\n" +
                    "    }\n" +
                    "}\n";
            FileUtil.writeBytes(content.getBytes(), filePath + serviceName);
        }
    }
    public static void outMybatisRouteFile(String fileName, String pack){
        String filePath = SRC + ROUTE_BASE_PATH + File.separator + ROUTE_API + File.separator + "pc" +File.separator + pack + File.separator;
        FileUtil.mkdir(filePath);
        String name = fileName.replace(".java", "");
        String apiName = name + "Api.java";
        String lowerCamelName = toLowerCamel(name);
        String header = "\n" +
                "import com.maplestone.labstudio.common.utils.HttpConst;\n" +
                "import com.maplestone.labstudio.common.utils.ResponseUtils;\n" +
                "import com.maplestone.labstudio.dto.PageDTO;\n" +
                "import com.maplestone.labstudio.entity."+pack+"."+name+";\n" +
                "import com.maplestone.labstudio.route.ApiConst;\n" +
                "import com.maplestone.labstudio.service."+pack+"."+name+"Service;\n" +
                "import io.swagger.annotations.Api;\n" +
                "import io.swagger.annotations.ApiOperation;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.beans.propertyeditors.CustomDateEditor;\n" +
                "import org.springframework.web.bind.ServletRequestDataBinder;\n" +
                "import org.springframework.web.bind.annotation.*;\n" +
                "\n" +
                "import java.text.SimpleDateFormat;\n" +
                "import java.util.Date;\n" +
                "import java.util.List;\n" +
                getComments()+
                "@RestController\n";
        String body = "    @Autowired\n" +
                "    "+name+"Service service;\n" +
                "\n" +
                "    @InitBinder\n" +
                "    public void initBinder(ServletRequestDataBinder binder) {\n" +
                "        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat(ApiConst.DATE_FORMAT),true));\n" +
                "    }\n" +
                "\n" +
                "    @GetMapping(\"/list/page\")\n" +
                "    @ApiOperation(\"查询全部\")\n" +
                "    public ResponseUtils page(PageDTO pageDTO) {\n" +
                "        return ResponseUtils.getSuccessResponseJoData(service.getPage(pageDTO));\n" +
                "    }\n" +
                "\n" +
                "    @GetMapping(\"/{id}\")\n" +
                "    @ApiOperation(\"修改回显\")\n" +
                "    public ResponseUtils findById(@PathVariable String id) {\n" +
                "        return ResponseUtils.getSuccessResponseJoData(service.findById(id));\n" +
                "    }\n" +
                "\n" +
                "    @PostMapping\n" +
                "    @ApiOperation(\"新增或修改\")\n" +
                "    public ResponseUtils saveOrUpdate(@RequestBody "+name+" "+lowerCamelName+") {\n" +
                "        return ResponseUtils.getStatusJoMsg(service.saveOrUpdate("+lowerCamelName+"), HttpConst.ALL_SUCCESS, HttpConst.ALL_FAILED);\n" +
                "    }\n" +
                "\n" +
                "    @DeleteMapping(\"/{id}\")\n" +
                "    @ApiOperation(\"根据id删除\")\n" +
                "    public ResponseUtils removeById(@PathVariable String id) {\n" +
                "        return ResponseUtils.getStatusJoMsg(service.removeById(id), HttpConst.DELETE_SUCCESS, HttpConst.DELETE_FAILED);\n" +
                "    }\n" +
                "\n" +
                "    @DeleteMapping(\"/ids\")\n" +
                "    @ApiOperation(\"根据id删除全部\")\n" +
                "    public ResponseUtils removeByIds(@RequestParam(\"ids[]\") List<String> ids) {\n" +
                "        return ResponseUtils.getStatusJoMsg(service.removeByIds(ids), HttpConst.DELETE_SUCCESS, HttpConst.DELETE_FAILED);\n" +
                "    }\n" +
                "}\n";
        File file = new File(filePath + apiName);
        if(!file.exists()){
            String content = "package com.maplestone.labstudio.route.api.pc."+pack+";\n" +
                    header+
                    "@RequestMapping(ApiConst.PC + \"/"+pack+"/"+name.toLowerCase()+"\")\n" +
                    "@Api(tags = ApiConst.PC_TAG +\""+pack+"-"+lowerCamelName+"\")\n" +
                    "public class "+name+"Api {\n" +
                    body;
            FileUtil.writeBytes(content.getBytes(), filePath + apiName);
        }

        filePath = SRC + ROUTE_BASE_PATH + File.separator + ROUTE_API + File.separator + "wx" +File.separator + pack + File.separator;
        String wxApiName = "WX" + name + "Api.java";
        File wxFile = new File(filePath + wxApiName);
        if(!wxFile.exists()){
            String content = "package com.maplestone.labstudio.route.api.wx."+pack+";\n" +
                    header+
                    "@RequestMapping(ApiConst.WX + \"/"+pack+"/"+name.toLowerCase()+"\")\n" +
                    "@Api(tags = ApiConst.WX_TAG +\""+pack+"-"+lowerCamelName+"\")\n" +
                    "public class WX"+name+"Api {\n" +
                    body;
            FileUtil.writeBytes(content.getBytes(), filePath + wxApiName);
        }
    }

    public static void outHibernateRouteFile(String fileName, String pack){
        String filePath = SRC + ROUTE_BASE_PATH + File.separator + ROUTE_API + File.separator + "pc" +File.separator + pack + File.separator;
        FileUtil.mkdir(filePath);
        String name = fileName.replace(".java", "");
        String apiName = name + "Api.java";
        String lowerCamelName = toLowerCamel(name);
        String header = "\n" +
                "import com.maplestone.labstudio.common.utils.HttpConst;\n" +
                "import com.maplestone.labstudio.common.utils.ResponseUtils;\n" +
                "import com.maplestone.labstudio.dto.PageDTO;\n" +
                "import com.maplestone.labstudio.entity."+pack+"."+name+";\n" +
                "import com.maplestone.labstudio.route.ApiConst;\n" +
                "import com.maplestone.labstudio.service."+pack+"."+name+"Service;\n" +
                "import io.swagger.annotations.Api;\n" +
                "import io.swagger.annotations.ApiOperation;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.beans.propertyeditors.CustomDateEditor;\n" +
                "import org.springframework.web.bind.ServletRequestDataBinder;\n" +
                "import org.springframework.web.bind.annotation.*;\n" +
                "\n" +
                "import java.text.SimpleDateFormat;\n" +
                "import java.util.Date;\n" +
                "import java.util.List;\n" +
                getComments()+
                "@RestController\n";
        String body = "    @Autowired\n" +
                "    "+name+"Service service;\n" +
                "\n" +
                "    @InitBinder\n" +
                "    public void initBinder(ServletRequestDataBinder binder) {\n" +
                "        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat(ApiConst.DATE_FORMAT),true));\n" +
                "    }\n" +
                "\n" +
                "    @GetMapping(\"/list/page\")\n" +
                "    @ApiOperation(\"查询全部\")\n" +
                "    public ResponseUtils page(PageDTO pageDTO) {\n" +
                "        return ResponseUtils.getSuccessResponseJoData(service.getPage(pageDTO));\n" +
                "    }\n" +
                "\n" +
                "    @GetMapping(\"/{id}\")\n" +
                "    @ApiOperation(\"修改回显\")\n" +
                "    public ResponseUtils findById(@PathVariable String id) {\n" +
                "        return ResponseUtils.getSuccessResponseJoData(service.findById(id));\n" +
                "    }\n" +
                "\n" +
                "    @PostMapping\n" +
                "    @ApiOperation(\"新增或修改\")\n" +
                "    public ResponseUtils saveOrUpdate(@RequestBody "+name+" "+lowerCamelName+") {\n" +
                "        return ResponseUtils.getStatusJoData(service.save("+lowerCamelName+"), HttpConst.ALL_FAILED);\n" +
                "    }\n" +
                "\n" +
                "    @DeleteMapping(\"/{id}\")\n" +
                "    @ApiOperation(\"根据id删除\")\n" +
                "    public ResponseUtils removeById(@PathVariable String id) {\n" +
                "        service.removeById(id);\n" +
                "        return ResponseUtils.getSuccessResponseJo();\n" +
                "    }\n" +
                "\n" +
                "    @DeleteMapping(\"/ids\")\n" +
                "    @ApiOperation(\"根据id删除全部\")\n" +
                "    public ResponseUtils removeByIds(@RequestParam(\"ids[]\") List<String> ids) {\n" +
                "        service.removeByIds(ids);\n" +
                "        return ResponseUtils.getSuccessResponseJo();\n" +
                "    }\n" +
                "}\n";

        File file = new File(filePath + apiName);
        if(!file.exists()){
            String content = "package com.maplestone.labstudio.route.api.pc."+pack+";\n" +
                    header +
                    "@RequestMapping(ApiConst.PC + \"/"+pack+"/"+name.toLowerCase()+"\")\n" +
                    "@Api(tags = ApiConst.PC_TAG +\""+pack+"-"+lowerCamelName+"\")\n" +
                    "public class "+name+"Api {\n" +
                    body;
            FileUtil.writeBytes(content.getBytes(), filePath + apiName);
        }

        filePath = SRC + ROUTE_BASE_PATH + File.separator + ROUTE_API + File.separator + "wx" +File.separator + pack + File.separator;
        String wxApiName = "WX" + name + "Api.java";
        File wxFile = new File(filePath + wxApiName);
        if(!wxFile.exists()){
            String content = "package com.maplestone.labstudio.route.api.wx."+pack+";\n" +
                    header +
                    "@RequestMapping(ApiConst.WX + \"/"+pack+"/"+name.toLowerCase()+"\")\n" +
                    "@Api(tags = ApiConst.WX_TAG +\""+pack+"-"+lowerCamelName+"\")\n" +
                    "public class WX"+name+"Api {\n" +
                     body;
            FileUtil.writeBytes(content.getBytes(), filePath + wxApiName);
        }
    }

    public static String getComments(){
        return  "\n" +
                "/**\n" +
                " * @author "+AUTHOR+"\n" +
                " */\n";
    }

    public static String toLowerCamel(String name){
        return StrUtil.lowerFirst(name);
    }
}
