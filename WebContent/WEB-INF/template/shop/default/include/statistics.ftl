[#escape x as x?html]
[#if setting.isCnzzEnabled && setting.cnzzSiteId?has_content]
	<span style="display: none;">
		<script type="text/javascript" src="https://pw.cnzz.com/c.php?id=${setting.cnzzSiteId?url}&l=2" charset="gb2312"></script>
	</span>
[/#if]
[/#escape]