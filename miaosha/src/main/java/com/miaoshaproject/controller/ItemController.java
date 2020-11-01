package com.miaoshaproject.controller;

import com.miaoshaproject.controller.VO.ItemVO;
import com.miaoshaproject.controller.VO.UserVO;
import com.miaoshaproject.error.BuinessException;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.model.ItemModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.miaoshaproject.controller.BaseController.CONTENT_TYPE_FORMED;

@Controller("item")
@RequestMapping("/item")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class ItemController {
    @Autowired
    ItemService itemService;
    //创建商品
    @RequestMapping(value = "/create", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createItem(
            @RequestParam(name = "title") String title,
            @RequestParam(name = "description") String description,
            @RequestParam(name = "price") BigDecimal price,
            @RequestParam(name = "stock")Integer stock,
            @RequestParam(name = "imgUrl")String imgUrl) throws BuinessException {

        //封装service请求用来创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setImgUrl(imgUrl);
        ItemModel item = itemService.createItem(itemModel);
        ItemVO itemVO = converVOFromModel(item);

        return CommonReturnType.creat(itemVO);
    }
    private ItemVO converVOFromModel(ItemModel itemModel){
        if(itemModel==null){
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel,itemVO);
        return itemVO;
    }

    //商品详情页
    @RequestMapping(value = "/get", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name = "id")Integer id) {
        ItemModel itemModel = itemService.getItemById(id);
        ItemVO itemVO = converVOFromModel(itemModel);
        return CommonReturnType.creat(itemVO);
    }

    //商品列表页面浏览
    @RequestMapping(value = "/list", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType listItem() {
        List<ItemModel> itemModelList = itemService.listItem();
        List<ItemVO> itemVOList = itemModelList.stream().map(itemModel -> {
            ItemVO itemVO = this.converVOFromModel(itemModel);
            return itemVO;
        }).collect(Collectors.toList());
        return CommonReturnType.creat(itemVOList);
    }
}
