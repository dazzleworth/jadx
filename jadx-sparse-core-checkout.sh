#!/bin/sh
# Copyright (c) 2018 Brendan Chong Email: opr_nexus@yahoo.com
# Script to update CFR decompiler
# Tested to work with Git Shell in Windows: 
# https://github-windows.s3.amazonaws.com/GitHubSetup.exe 
# No permission required in normal Windows environment

jadx_version_file="./core.version"
jadx_core_rep=https://github.com/skylot/jadx.git
tmp_path=tmp
src_path=($tmp_path "jadx-core" "src" "main" "java" "jadx")
dest_path=("app" "src" "main" "java")
cur_commit=$(git rev-parse HEAD | cut -c1-10)
cur_remote_commit=$(git ls-remote "$jadx_core_rep" HEAD | head -1 | cut -c1-10)

if [ "$cur_remote_commit" = "$cur_commit" ] && [ ! -f $jadx_version_file ];
then
	echo 'You just cloned this repository. Core should be latest.'
	exit 0
fi

if [ -f $jadx_version_file ]
then
	IFS= read -r -n 36 core_curr_commit < $jadx_version_file
	if [ "$cur_remote_commit" = "$core_curr_commit" ]
	then
		echo 'Core is already updated.'
		exit 0
	fi
fi

mkdir $tmp_path
cd "$tmp_path$ds"
git init
git remote add -f origin $jadx_core_rep
git pull origin master

src_path=$(printf '%s/' "${src_path[@]%/}" )
dest_path=$(printf '%s/' "${dest_path[@]%/}" )

cd ..

rm -r "${dest_path}jadx"

mv -v $src_path $dest_path

echo $cur_remote_commit > $jadx_version_file

rm -rf "$tmp_path"

git add .

git commit -m $(printf 'Upgraded JADX-core to %s...' ) #| cut -c 1-7 )