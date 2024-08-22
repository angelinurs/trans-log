FROM rockylinux:8.8

ENTRYPOINT ["/usr/sbin/init"]

ENV WORK_HOME /app
ENV TZ=Asia/Seoul

ENV USER naru
ENV DIRECTORY /home/${USER}/storage

RUN set -eux; \
    ln -Tfs /usr/share/zoneinfo/Asia/Seoul /etc/localtime;

RUN set -eux; \
    sed -i 's/mirrorlist=/#mirrorlist=/g' /etc/yum.repos.d/{Rocky-BaseOS.repo,Rocky-AppStream.repo} &&\
    sed -i 's/#baseurl=http:\/\/dl.rockylinux.org/baseurl=http:\/\/ftp.kaist.ac.kr/g' /etc/yum.repos.d/{Rocky-BaseOS.repo,Rocky-AppStream.repo};

RUN dnf -y update && \
    dnf -y install \
    openssh-server \
    sudo \
    && dnf clean all

# SSH 서버 설정
RUN mkdir /var/run/sshd && \
    echo 'root:rootpassword' | chpasswd  # root 계정 비밀번호 설정

# 사용자 'naru' 추가 및 비밀번호 설정
RUN useradd -m naru && \
    echo 'naru:naru0914' | chpasswd && \
    usermod -aG wheel naru

# SSH 서버의 비밀번호 인증을 허용하도록 설정
RUN sed -i 's/^#PasswordAuthentication yes/PasswordAuthentication yes/' /etc/ssh/sshd_config
RUN mkdir /home/naru/storage

RUN sudo -u $USER mkdir -p $DIRECTORY && \
    sudo chown $USER:$USER $DIRECTORY && \
    sudo chmod 755 $DIRECTORY

EXPOSE 22

CMD ["/usr/sbin/sshd", "-D"]