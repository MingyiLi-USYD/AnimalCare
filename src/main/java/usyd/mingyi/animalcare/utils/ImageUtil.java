package usyd.mingyi.animalcare.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.*;


public class ImageUtil {


    /* 把Base64字符串解码成图片并存到本地*/
    public static void convertBase64ToFile(String fileBase64String, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            //判断文件目录是否存在
            if (!dir.exists()) {
                System.out.println("创建成功");
                dir.mkdirs();
            }
            //byte[] bfile = Base64.getDecoder().decode(fileBase64String);
            byte[] bfile = Base64.decodeBase64(fileBase64String);
            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return;
    }



    /*把本地图片编码成base64字符串*/
    public static String ImageToBase64ByLocal(String imgFile) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理


        InputStream in = null;
        byte[] data = null;

        // 读取图片字节数组
        try {
            in = new FileInputStream(imgFile);

            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
           // e.printStackTrace();
        }


        return Base64.encodeBase64String(data);

    }

    /*校验字符串是不是base64字符串*/
    public static boolean checkImage(String base64Data){
        String dataPrix = ""; //base64格式前头
        String data = "";//实体部分数据
        if(base64Data==null||"".equals(base64Data)){

            return false;
        }else {
            String [] d = base64Data.split("base64,");//将字符串分成数组
            if(d != null && d.length == 2){
                dataPrix = d[0];
                data = d[1];
            }else {

                return false;

            }
        }
        String suffix = "";//图片后缀，用以识别哪种格式数据
        //data:image/jpeg;base64,base64编码的jpeg图片数据
        if("data:image/jpeg;".equalsIgnoreCase(dataPrix)){
            suffix = ".jpg";
        }else if("data:image/x-icon;".equalsIgnoreCase(dataPrix)){
            //data:image/x-icon;base64,base64编码的icon图片数据
            suffix = ".ico";
        }else if("data:image/gif;".equalsIgnoreCase(dataPrix)){
            //data:image/gif;base64,base64编码的gif图片数据
            suffix = ".gif";
        }else if("data:image/png;".equalsIgnoreCase(dataPrix)){
            //data:image/png;base64,base64编码的png图片数据
            suffix = ".png";
        }else {

            return false;
        }
        return true;
    }

    /* 获取base64字符串的数据部分*/
    public static String getData(String base64Data){
        String dataPrix = ""; //base64格式前头
        String data = "";//实体部分数据
        String [] d = base64Data.split("base64,");//将字符串分成数组
        if(d != null && d.length == 2){
            dataPrix = d[0];
            data = d[1];
        }
        return data;
    }

    /* 获取字符串的后缀部分*/
    public static String getSuffix(String base64Data){
        String dataPrix = ""; //base64格式前头
        String data = "";//实体部分数据
            String [] d = base64Data.split("base64,");//将字符串分成数组
            if(d != null && d.length == 2){
                dataPrix = d[0];
                data = d[1];
            }

        String suffix = "";//图片后缀，用以识别哪种格式数据
        //data:image/jpeg;base64,base64编码的jpeg图片数据
        if("data:image/jpeg;".equalsIgnoreCase(dataPrix)){
            suffix = ".jpg";
        }else if("data:image/x-icon;".equalsIgnoreCase(dataPrix)){
            //data:image/x-icon;base64,base64编码的icon图片数据
            suffix = ".ico";
        }else if("data:image/gif;".equalsIgnoreCase(dataPrix)){
            //data:image/gif;base64,base64编码的gif图片数据
            suffix = ".gif";
        }else if("data:image/png;".equalsIgnoreCase(dataPrix)){
            //data:image/png;base64,base64编码的png图片数据
            suffix = ".png";
        }
        return suffix;
    }

   /* *//*数据库保存的是图片的名字,需要替换图片名字为base64数据*//*
    public static List<Post> replaceUrl(List<Post> allPosts,String path){
        for (int i = 0; i < allPosts.size(); i++) {

            Post post = allPosts.get(i);
            List<String> imageUrlList = post.getImageUrlList();

            String userAvatar = post.getUserAvatar();

            for (int j = 0; j < imageUrlList.size(); j++) {
                String fileName = imageUrlList.get(j);

                imageUrlList.set(j,ImageUtil.ImageToBase64ByLocal(path+File.separator+fileName));
            }
            if(userAvatar.equals("default.jpg")){
                //表明现在用户还是用的默认头像
                //获取到上级路径
                String rootPath = path.substring(0, path.lastIndexOf("/")+1);
               //System.out.println(rootPath);
                post.setUserAvatar(ImageUtil.ImageToBase64ByLocal(rootPath+userAvatar));
            }else
             {
                  //表面已经更改过默认头像 自己的头像存在自己独立的文件夹里面
                post.setUserAvatar(ImageUtil.ImageToBase64ByLocal(path+File.separator+userAvatar));
            }

            post.setImageUrlList(imageUrlList);
            allPosts.set(i,post);

        }
        return allPosts;
    }

    public static Post replaceUrl(Post post,String path){

            List<String> imageUrlList = post.getImageUrlList();

            String userAvatar = post.getUserAvatar();

            for (int j = 0; j < imageUrlList.size(); j++) {

                String fileName = imageUrlList.get(j);

                imageUrlList.set(j,ImageUtil.ImageToBase64ByLocal(path+File.separator+fileName));
            }

            post.setImageUrlList(imageUrlList);

            if(userAvatar.equals("default.jpg")){
                //表明现在用户还是用的默认头像
                //获取到上级路径
                String rootPath = path.substring(0, path.lastIndexOf("/")+1);
                //System.out.println(rootPath);
                post.setUserAvatar(ImageUtil.ImageToBase64ByLocal(rootPath+userAvatar));
            }else {
                //表面已经更改过默认头像 自己的头像存在自己独立的文件夹里面
                post.setUserAvatar(ImageUtil.ImageToBase64ByLocal(path+File.separator+userAvatar));
            }

        return post;
    }

    public static Comment replaceAvatarUrl(Comment comment, String path) {

        if(comment.getUserAvatar().equals("default.jpg")){//表明现在用户还是用的默认头像
            //获取到上级路径
            String rootPath = path.substring(0, path.lastIndexOf("/")+1);
            comment.setUserAvatar(ImageUtil.ImageToBase64ByLocal(rootPath+comment.getUserAvatar()));
        }else {//表面已经更改过默认头像 自己的头像存在自己独立的文件夹里面
            comment.setUserAvatar(ImageUtil.ImageToBase64ByLocal(path+File.separator+comment.getUserAvatar()));
        }
        return comment;
    }

    public static List<Comment> replaceAvatarUrl(List<Comment> allComment, String path) {

        for (Comment comment : allComment) {
            if (comment.getUserAvatar().equals("default.jpg")) {//表明现在用户还是用的默认头像
                //获取到上级路径
                String rootPath = path.substring(0, path.lastIndexOf("/") + 1);
                comment.setUserAvatar(ImageUtil.ImageToBase64ByLocal(rootPath + comment.getUserAvatar()));
            } else {//表面已经更改过默认头像 自己的头像存在自己独立的文件夹里面
                comment.setUserAvatar(ImageUtil.ImageToBase64ByLocal(path + File.separator + comment.getUserAvatar()));
            }
        }
        return allComment;
    }

    public static void replacePetUrl(List<Pet> allPets,String path){
        for (int i = 0; i < allPets.size(); i++) {

            Pet pet = allPets.get(i);
            String fileName = pet.getPetImageAddress();

            if(fileName.equals("catDefault.jpg")||fileName.equals("dogDefault.jpg")){
                //表明现在用户还是用的默认头像
                //获取到上级路径
                String rootPath = path.substring(0, path.lastIndexOf("/")+1);
                System.out.println(rootPath);
                pet.setPetImageAddress(ImageUtil.ImageToBase64ByLocal(rootPath+fileName));
            }else {
                //表面已经更改过默认头像 自己的头像存在自己独立的文件夹里面
                pet.setPetImageAddress(ImageUtil.ImageToBase64ByLocal(path+File.separator+fileName));
            }

        }

    }
    public static void replacePetUrl(Pet pet,String path){
            String fileName = pet.getPetImageAddress();

            if(fileName.equals("catDefault.jpg")||fileName.equals("dogDefault.jpg")){
                //表明现在用户还是用的默认头像
                //获取到上级路径
                String rootPath = path.substring(0, path.lastIndexOf("/")+1);
                System.out.println(rootPath);
                pet.setPetImageAddress(ImageUtil.ImageToBase64ByLocal(rootPath+fileName));
            }else {
                //表面已经更改过默认头像 自己的头像存在自己独立的文件夹里面
                pet.setPetImageAddress(ImageUtil.ImageToBase64ByLocal(path+File.separator+fileName));
            }

        List<String> imageUrlList = pet.getPetImageList();
        if(imageUrlList==null)return;
        for (int j = 0; j < imageUrlList.size(); j++) {

            fileName = imageUrlList.get(j);

            imageUrlList.set(j,ImageUtil.ImageToBase64ByLocal(path+File.separator+fileName));
        }


    }
    public static void replaceUserUrl(User user, String path){
        String userImageAddress = user.getUserImageAddress();
        if(userImageAddress.equals("default.jpg")){
            //表明现在用户还是用的默认头像
            //获取到上级路径
            String rootPath = path.substring(0, path.lastIndexOf("/")+1);
            //System.out.println(rootPath);
            user.setUserImageAddress(ImageUtil.ImageToBase64ByLocal(rootPath+userImageAddress));
        }else {
            //表面已经更改过默认头像 自己的头像存在自己独立的文件夹里面
            user.setUserImageAddress(ImageUtil.ImageToBase64ByLocal(path+File.separator+userImageAddress));
        }

        List<Post> postList = user.getPostList();
        for (int i = 0; i < postList.size(); i++) {

            Post post = postList.get(i);
            List<String> imageUrlList = post.getImageUrlList();
            for (int j = 0; j < imageUrlList.size(); j++) {
                String fileName = imageUrlList.get(j);

                imageUrlList.set(j,ImageUtil.ImageToBase64ByLocal(path+File.separator+fileName));
            }
        }
        List<Pet> petList = user.getPetList();
        replacePetUrl(petList,path);

    }
*/

  /*  *//* 流处理*//*
    public static byte[]  getBytesByStream(InputStream inputStream){
        byte[] bytes = new byte[1024];

        int b;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            while((b = inputStream.read(bytes)) != -1){

                byteArrayOutputStream.write(bytes,0,b);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
*/



}
