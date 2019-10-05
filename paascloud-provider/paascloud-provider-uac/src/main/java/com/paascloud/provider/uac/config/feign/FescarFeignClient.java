/*
 * Copyright (C) 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.paascloud.provider.uac.config.feign;

import com.paascloud.common.util.UserContext;
import feign.Client;
import feign.Request;
import feign.Response;
import io.seata.core.context.RootContext;
import org.springframework.util.StringUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.util.*;

/**
 * 
 * @author zcb
 * @date 2019年4月9日 上午10:26:42
 */
public class FescarFeignClient extends Client.Default {


	/**
	 * Null parameters imply platform defaults.
	 *
	 * @param sslContextFactory
	 * @param hostnameVerifier
	 */
	public FescarFeignClient(SSLSocketFactory sslContextFactory, HostnameVerifier hostnameVerifier) {
		super(sslContextFactory, hostnameVerifier);
	}

	@Override
	public Response execute(Request request, Request.Options options) throws IOException {
		request = getModifyRequest(request);
		return super.execute(request, options);
	}

	@SuppressWarnings("deprecation")
	private Request getModifyRequest(Request request) {

		String xid = RootContext.getXID();
		String userInfo = UserContext.get();
		if (StringUtils.isEmpty(xid) && StringUtils.isEmpty(userInfo)) {
			return request;
		}

		Map<String, Collection<String>> headers = new HashMap<>();
		headers.putAll(request.headers());
		if (!StringUtils.isEmpty(xid)) {
			List<String> fescarXid = new ArrayList<>();
			fescarXid.add(xid);
			headers.put(RootContext.KEY_XID, fescarXid);
		}
		if (!StringUtils.isEmpty(userInfo)) {
			List<String> user = new ArrayList<>();
			user.add(userInfo);
			headers.put(UserContext.key, user);
		}
		return Request.create(request.method(), request.url(), headers, request.body(),
				request.charset());
	}

}