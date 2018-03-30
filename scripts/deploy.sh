#!/usr/bin/env bash

create_release() {
	set -x
	curl --progress-bar \
		-u spinningarrow:$GITHUB_RELEASE_TOKEN \
		-d '{ "tag_name": "'$tag_name'" }' \
		https://api.github.com/repos/spinningarrow/pimote-api/releases
	set +x
}

parse_upload_url() {
	egrep -o 'upload_url":\s?".+"' | cut -d':' -f2- | cut -d'{' -f1 | cut -d'"' -f2
}

upload_artifact() {
	set -x
	local upload_url=$1
	local asset_file=$2

	curl --progress-bar \
		-u spinningarrow:$GITHUB_RELEASE_TOKEN \
		-H 'Content-Type: application/javascript' \
		--data-binary @${asset_file} \
		"${upload_url}?name=pimote-api.jar"
	set +x
}

parse_download_url() {
	egrep -o 'browser_download_url":\s?".+"' | cut -d':' -f2- | cut -d'"' -f2
}

publish_deployment_message() {
	set -x
	local download_url=$1
	local publish_url='https://ps.pndsn.com/publish'
	local channel='pimote-deployments'

	curl --progress-bar \
		-H 'Content-Type: application/json' \
		-d '{"name": "pimote-api", "url": "'${download_url}'"}' \
		"${publish_url}/${PUBNUB_PUBLISH_KEY}/${PUBNUB_SUBSCRIBE_KEY}/0/${channel}/0?uuid=${PUBNUB_UUID}"
	set +x
}

asset_file=$(ls target/uberjar/*standalone.jar)
test -z ${asset_file} && exit 1

app_version=$(echo '(System/getProperty "pimote-api.version")' | lein repl | tail -n 2 | head -n 1 | cut -d'"' -f2)
commit_hash=$(git log -n1 --pretty=%h)
tag_name="${app_version}-${commit_hash}"

echo Creating release $tag_name
upload_url=$(create_release | parse_upload_url)
test -z ${upload_url} && exit 1
echo ${upload_url}

echo

echo Uploading artifact ${asset_file}
download_url=$(upload_artifact ${upload_url} ${asset_file} | parse_download_url)
test -z ${download_url} && exit 1
echo ${download_url}

echo Publishing message
publish_deployment_message ${download_url}
