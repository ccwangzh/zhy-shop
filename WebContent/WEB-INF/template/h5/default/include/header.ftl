			[#escape x as x?html]
			<link href="${base}/resources/h5/${theme}/css/header.css" rel="stylesheet" type="text/css" />
			<script type="text/javascript">
				$(function(){
				
				var $search=$("#search");
				var $searchBtn=$("#searchBtn");
				var $clearHistory=$("#clearHistory");
				var $searchKeyword=$("#searchKeyword");
				var $searchHistory=[];
				if(localStorage.searchHistory){
					$searchHistory=JSON.parse(localStorage.searchHistory);
					var html="";
					$.each($searchHistory,function(index,value){
						html+="<li><a class='external' href='${h5Base}/goods/search.jhtml?keyword="+value+"'>"+value+"</a></li>";
					})
					$("#searchHistory").find("ul").html(html);
				}else{
					$("#searchHistory").hide();
				}
				
				
				$search.focus(function(){
					Zepto.popup(".popup-search");
				})
				
				$searchBtn.click(function(){
					var keyword=$searchKeyword.val();
					if(keyword=="" || keyword==null){
						Zepto.toast("请输入搜索关键字",1000)
					}else{
						if($.inArray(keyword,$searchHistory)<0){
							$searchHistory.unshift(keyword);
						}
						localStorage.searchHistory=JSON.stringify($searchHistory);
						location.href="/shop/h5/goods/search.jhtml?keyword="+keyword;
					}
				})
				
				//监听浏览器滚动
				   $("#maincon").scroll(function(){  
                 
                    if($("#maincon").scrollTop()>0)
                    {  
                       $(".bar").css("opacity","1")
                     } else{
                        $(".bar").css("opacity","0.5")
                     }
                      
                    })  
				
				$clearHistory.click(function(){
					$("#searchHistory").hide().find("ul").html("");
					localStorage.searchHistory="";
				})
				
				
				var $productTypeItem=$("#productType .typeLeft li")
				var $pullRight=$(".pull-right")
				var $closeRight=$("#closeRight")
				$pullRight.click(function(){
					$("#productType").addClass('show');
				})
				$closeRight.click(function(){
					$("#productType").removeClass('show');
				})
				$productTypeItem.click(function(){
					var index=$(this).index();
					$(this).addClass('cur').siblings("li").removeClass('cur');
					$(".typeRight .sub").eq(index).addClass("cur").siblings().removeClass("cur");
				}).eq(0).trigger("click");
				
				var $subItem=$(".typeRight .item-content dt");
				$subItem.click(function(){
					
						$(this).siblings('dd').slideToggle();
					
					
				})
				
				})
			</script>
			<header class="bar bar-nav">
					<a class="button button-link button-nav pull-left open-panel" data-panel="#panel-left-demo">
						<i class="font font-lists"></i>
					</a>
					<a class="button button-link button-nav pull-right">
						<i class="font font-rightdaohang"></i>
					</a>
					<div class="searchbar title">
						<div class="search-input">
							<label class="icon icon-search" for="search"></label>
							<input type="search" id="search" placeholder="输入关键字...">
						</div>
					</div>
			</header>
			<!--分类导航-->
			[@product_category_root_list count = 6]
			<div  class="productType" id="productType">
			<header class="bar bar-nav">
			  <a class="icon icon-left pull-left cofff" id="closeRight"></a>
			  <h1 class="title biaoti">商品分类</h1>
			</header>
            <div class="typeLeft">
                <ul>
                	[#list productCategories as productCategory]
                    	<li class="typeSelected">
                    		[@product_category_children_list productCategoryId = productCategory.id recursive = false count = 3]
                    			<a href="#"><p>${productCategory.name}</p></a>
                    		[/@product_category_children_list]
                    	</li>
                    [/#list]
                   
                </ul>
            </div>
            <div class="typeRight">
            
            [#list productCategories as productCategory]
            [@product_category_children_list productCategoryId = productCategory.id recursive = false count = 8]
            	<div class="sub">
            	[#list productCategories as productCategory]
            	
                <div class="item">
                    <div class="item-content">
                        <dl>
                        	<dt>${productCategory.name}<i class="font font-fr"></i></dt>
                        	[@product_category_children_list productCategoryId = productCategory.id recursive = false]
                        		
                            		<dd>
                            			<ul>
                            				[#list productCategories as productCategory]
                            					<li><a class="external" href="${base}/h5/${productCategory.path}">${productCategory.name}</a></li>
                            				[/#list]
                            			</ul>
                            		</dd>
                            	
                            [/@product_category_children_list]
                        </dl>
                    </div>
                </div>
                [/#list]
                </div>
            [/@product_category_children_list]
            [/#list]
            
            </div>
        </div>
			[/@product_category_root_list]
			<div class="popup popup-search productSearch">
			  
			  		<!--搜索栏-->
			    	<div style="z-index:20" class="bar bar-header-secondary">
			            <div class="searchbar">
			                <a class="searchbar-cancel button button-fill" id="searchBtn">搜索</a>
			                <div class="search-input">
			                    <label class="icon icon-search" for="search"></label>
			                    <input type="search" id='searchKeyword' placeholder='输入关键字...' x-webkit-speech/>
			                </div>
			            </div>
			        </div>
			        <!--最近搜索-->
			        
			        
			        <div class="recentSearch" id="searchHistory">
			            <div class="rs-header">
			                <span>最近搜索</span>
			                <a href="#" id="clearHistory" class="font font-delete"></a>
			            </div>
			            <div class="rs-content">
			                <ul>
			              
			                </ul>
			            </div>
			        </div>
			       
			        
			        <!--热门搜索-->
			        [#if setting.hotSearches?has_content]
			        <div class="hotSearch">
			            <div class="rs-header">
			            	
			                <span>${message("shop.header.hotSearch")}</span>
			                
			            </div>
			            <div class="rs-content">
			                <ul>
			                	[#list setting.hotSearches as hotSearch]
			                    	<li><a class="external" href="${base}/h5/goods/search.jhtml?keyword=${hotSearch?url}">${hotSearch}</a></li>
			                    [/#list]
			                </ul>
			            </div>
			        </div>
			        [/#if]
			        
			        <i class="font font-close close-popup"></i>

			</div>
			[/#escape]