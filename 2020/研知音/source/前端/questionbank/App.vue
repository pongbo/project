<script>
	import Vue from 'vue'
	import {plat} from 'utils/conmmon.js'
	import {
		mapMutations
	} from 'vuex'
	export default {
		onLaunch: function() {
			uni.getSystemInfo({
				success: function(e) {
					console.log(e)
					// #ifdef H5
					if(e.windowWidth > 760){
						uni.showModal({
							content:'请用移动端设备访问',
							showCancel:false
						});
					}
					// #endif
					// #ifndef MP
					Vue.prototype.StatusBar = e.statusBarHeight;
					if (e.platform == 'android') {
						Vue.prototype.CustomBar = e.statusBarHeight + 50;
					} else {
						Vue.prototype.CustomBar = e.statusBarHeight + 45;
					};
					// #endif
					// #ifdef MP-WEIXIN
					Vue.prototype.StatusBar = e.statusBarHeight;
					let custom = wx.getMenuButtonBoundingClientRect();
					Vue.prototype.Custom = custom;
					Vue.prototype.CustomBar = custom.bottom + custom.top - e.statusBarHeight;
					// #endif       
					// #ifdef MP-ALIPAY
					Vue.prototype.StatusBar = e.statusBarHeight;
					Vue.prototype.CustomBar = e.statusBarHeight + e.titleBarHeight;
					// #endif
					//窗口高度
					Vue.prototype.DeviceHight = e.windowHeight;
				}
			})
			let audioPlayer = null;
			let timer = null;
			if(plat === 'H5'){
				audioPlayer = uni.createInnerAudioContext()
			}else{
				audioPlayer = uni.getBackgroundAudioManager()
			}
			
			//注册事件函数写在全局或者写在vuex（不然重复绑定，内存泄露）里都行，这里图方便挂着全局
			Vue.prototype.$au_player = audioPlayer;
			Vue.prototype.$au_player.onPlay(()=>{
				console.log('playing')
				Vue.prototype.cusPlay && Vue.prototype.cusPlay()
				clearInterval(timer)
				timer = setInterval(()=>{ //安卓和ios app 下onTimeUpdate事件在替换资源和seek之后不会触发，这里做手动触发
					//console.log('update')
					Vue.prototype.cusTimeUpdate()
				},240)
			})
			// Vue.prototype.$au_player.onTimeUpdate(()=>{
			// 	console.log('update')
			// 	Vue.prototype.cusTimeUpdate && Vue.prototype.cusTimeUpdate()
			// })
			Vue.prototype.$au_player.onEnded(()=>{
				Vue.prototype.cusEnded && Vue.prototype.cusEnded()
				clearInterval(timer)
			})
			Vue.prototype.$au_player.onError((err)=>{
				console.log('play err:'+err)
				this.setIsplayactive(false)
				clearInterval(timer)
			})
			Vue.prototype.$au_player.onStop((res)=>{
				console.log('play stop:'+res)
				this.setIsplayactive(false)
				clearInterval(timer)
			})
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		},
		onError(e) {
			console.log(e);
		},
		methods:{
			...mapMutations(['setIsplayactive'])
		}
	}

		

</script>

<style lang="scss">
	@import "colorui/main.css";
	@import "colorui/icon.css";
	@import "./cool/ui/static/css/index.scss";
	@import "./common/uni.css";

@import "./static/css/index.scss";
@import "uview-ui/index.scss";

page {
	background-color: #f7f7f7;
}
</style>
