<template>
	<view class="w-100">
		<view class="w-100 flex_wrap">
			<view class="imgs-view" v-for="(v,i) in imgArray" :key="i">
				<image @click="preview(v,i)" :src="v"></image>
				<view class="del-btn" @click="delImg(i)">
					<image src="@/static/delete.png"></image>
				</view>
				<view class="uploading flex_xy_center" v-if="closeLoading&&startUpload">
					<view><image src="@/static/uploading4.png"></image>
					<view class="upload-txt">上传中...</view></view>
				</view>
			</view>
			<view v-if="imgArray.length<imgCount" class="upload-img-view flex_xy_center" @click="upPhoto">
				<image src="@/static/jia.png"></image>
			</view>
		</view>
		<view v-if="!closeTip" class="tip">* 最多上传{{imgCount}}张图片(<label> {{imgArray.length}} </label>/{{imgCount}})</view>
	</view>
</template>

<script>
	//import $config from "@/appConfig.js"; //调用url前缀 *注意修改为自己的！！！
	export default {
		name: 'imgUpload',
		props: {
			imgArr: { //图片数组
				type: [Array],
			},
			uploadImgCount: { //一次上传图片数
				type: String,
				default: '3'
			},
			imgCount: { //可上传图片总数
				type: String,
				default: '3'
			},
			imgSize: { //图片大小 单位M
				type: Number,
				default: 2
			},
			imgType: {
				type: [Array],
				default: function() {
					return ['jpeg', 'png', 'jpg']
				}
			},
			closeTip: {
				type: Boolean,
				default: false
			},
			closeLoading: {
				type: Boolean,
				default: true
			},
		},
		data() {
			return {
				imgArray: [],
				canUpCount:'',
				startUpload:false,
			}
		},
		created() {
			this.imgArray = this.imgArr;
			this.canUpCount = Number(this.uploadImgCount);
			// this.uploadImgUrl = '@/static/uploading'+this.uploadImgStyle+'.png';
		},
		watch: {
			imgArr(n,o){
				this.imgArray = n;
			},
			// uploadImgStyle(n,o){
			// 	this.uploadImg = '@/static/uploading'+n+'.png';
			// }
		},
		methods: {
			upPhoto() {
				let that = this;
				if (Number(that.imgCount - that.imgArray.length) < Number(that.uploadImgCount)) {
					that.canUpCount = Number(that.imgCount - that.imgArray.length);
				}
				uni.showActionSheet({
					itemList: ['拍照上传', '从相册中选择'],
					success: function(res) {
						if (res.tapIndex == 0) {
							uni.chooseImage({
								count: Number(that.canUpCount),
								sizeType: ['original', 'compressed'], //可以指定是原图还是压缩图，默认二者都有
								sourceType: ['camera'], //从相册选择
								success: function(res) {
									if (res) {
										if (res.tempFiles instanceof Array && res.tempFiles) {
											for (let item of res.tempFiles) {
												if (item.size > (that.imgSize * 1024 * 1024)) {
													uni.showToast({
														title: `图片不能大于${that.imgSize}M`,
														icon: 'none'
													})
													return false;
												}
												let r = that.imgType.some(v => {
													let type = item.type.split('/');
													if (type.length)
														return (v === type[1]);
												});
												if (!r) {
													uni.showToast({
														title: `只允许上传${that.imgType}的类型`,
														icon: 'none'
													})
													return false;
												}
											}
										}
										that.imgArray = [...that.imgArray, ...res.tempFilePaths];
									}
								}
							});
						} else if (res.tapIndex == 1) {
							uni.chooseImage({
								count: Number(that.canUpCount),
								sizeType: ['original', 'compressed'], //可以指定是原图还是压缩图，默认二者都有
								sourceType: ['album'], //从相册选择
								success: function(res) {
									if (res) {
										if (res.tempFiles instanceof Array && res.tempFiles) {
											for (let item of res.tempFiles) {
												if (item.size > (that.imgSize * 1024 * 1024)) {
													uni.showToast({
														title: `图片不能大于${that.imgSize}M`,
														icon: 'none'
													})
													return false;
												}
												let r = that.imgType.some(v => {
													let type = item.type.split('/');
													if (type.length)
														return (v === type[1]);
												});
												if (!r) {
													uni.showToast({
														title: `只允许上传${that.imgType}的类型`,
														icon: 'none'
													})
													return false;
												}
											}
										}
										that.imgArray = [...that.imgArray, ...res.tempFilePaths];
									}
								}
							});
						}
					},
					fail: function(res) {
						console.log(res.errMsg);
					}
				});
			},
			preview(url, index) {
				// 预览图片
				uni.previewImage({
					urls: [url]
				});
			},
			delImg(i) {
				const _this = this;
				uni.showModal({
					title: '提示',
					content: '是否删除这张照片？',
					success: function(res) {
						if (res.confirm) {
							_this.imgArray.splice(i, 1);
						} else if (res.cancel) {}
					}
				});
			},


			upload(callback) {
				const _this = this;
				if (_this.imgArray instanceof Array && _this.imgArray.length) {
					this.startUpload=true;
					let successNum = 0;
					let urlArr = [];
					for (let item of _this.imgArray) {
						_this.uploadImg(item, res => {
							if (res.code == 0) {
								successNum++;
								urlArr.push(res.url);
							} else {
								urlArr.push(res);
							}
							if (urlArr.length == _this.imgArray.length) {
								if(successNum>0){
									this.startUpload=false;
									callback(_this.result(urlArr, successNum));
								}else{
									this.startUpload=false;
									callback(urlArr); //错误的返回
								}
							}
						});
					}
				} else {
					callback({
						result: 'warning',
						code: 400,
						msg: '请先上传图片！'
					});
				}
			},

			result(urlArr, successNum) {
				let result = {
					result: 'success',
					code: 0,
					urlArray: urlArr,
					success: successNum
				}
				return result;
			},

			uploadImg(item, callback) {
				const _this = this;
				uni.uploadFile({
					url: 'api/sys/user/modify/uploadPic', //自行修改各自的对应的接口 
					filePath: item,
					name: 'file',
					success: (uploadFileRes) => {
						if (uploadFileRes) {
							let res = JSON.parse(uploadFileRes.data);
							callback(res);
						}
					},
					fail:(e)=>{
						callback({
							code:500,
							msg:'图片上传失败',
							localUrl:item,
							reason:e
						});
					}
				});
			}
		}
	}
</script>

<style scoped>
	.w-100 {
		width: 100%;
	}

	.flex {
		/* 转为弹性盒模型*/
		display: flex;
	}

	.flex_bet {
		/* 两端左右*/
		display: flex;
		justify-content: space-between;
	}

	.flex_wrap {
		/* 转为弹性盒模型并自动换行*/
		display: flex;
		flex-wrap: wrap;
	}
	
	.flex_xy_center{
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.upload-img-view {
		height: 200upx;
		width: 32%;
		border-radius: 10upx;
		border: 4upx dotted #F1F1F1;
		/* background-color: #F1F1F1; */
	}

	.upload-img-view>image {
		width: 70upx;
		height: 70upx;
	}

.upload-txt{
	font-size: 24upx;
	color: #FFFFFF;
}
	.imgs-view {
		height: 200upx;
		width: 31.5%;
		border-radius: 10upx;
		margin-right: 1.8%;
		margin-bottom: 16upx;
		border: 1upx solid #F1F1F1;
		box-sizing: border-box;
		position: relative;
	}

	.uploading{
		position: absolute;
		background-color: rgba(0,0,0,.5);
		left: 0;
		top: 0;
		width: 100%;
		height: 100%;
		text-align: center;
		line-height: 100%;
		z-index: 999;
	}
	
	.uploading image{
		width: 60upx;
		height: 60upx;
		z-index: 1000;
		animation: rotation .6s linear infinite;
		-moz-animation: rotation .6s linear infinite;
		-webkit-animation: rotation .6s linear infinite;
		-o-animation: rotation .6s linear infinite;
	}
	
	
	@keyframes rotation {
		from {
			-webkit-transform: rotate(0deg);
			transform: rotate(0deg);
			-moz-transform: rotate(0deg);
			-o-transform: rotate(0deg);
		}
	
		to {
			-webkit-transform: rotate(360deg);
			transform: rotate(360deg);
			-moz-transform: rotate(360deg);
			-o-transform: rotate(360deg);
		}
	}
	
	.imgs-view>image {
		width: 100%;
		height: 100%;
		border-radius: 10upx;
	}

	.tip {
		font-size: 24upx;
		color: #FF0000;
		margin-top: 12upx;
	}

	.tip>label {
		color: #009100;
	}

	.del-btn {
		position: absolute;
		top: 0;
		right: 0;
		width: 32upx;
		height: 32upx;
		z-index: 999;
	}

	.del-btn>image {
		width: 100%;
		height: 100%;
		display: flex;
	}
</style>
