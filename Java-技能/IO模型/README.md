# IO模型

主要有四种基本的I/O模型：同步阻塞I/O（Synchronous Blocking I/O）、同步非阻塞I/O（Synchronous Non-Blocking
I/O）、异步I/O（Asynchronous I/O），以及信号驱动I/O（Signal-driven I/O）。但在实际应用中，通常是前三种模型

## BIO/NIO/AIO区别

### 同步阻塞IO，BIO

当一个线程发起一个I/O请求时，它会阻塞并等待直到I/O操作完成。在此期间，线程不能执行其他任务。这种模型适用于I/O操作较少且操作时间较短的场景。

- java中应用：使用java.io包中的类，如InputStream和OutputStream。它是基于同步阻塞I/O的

### 同步非阻塞IO，NIO

在这种模型中，当一个线程发起一个I/O请求时，它不会立即阻塞。如果I/O操作不能立即完成，线程会立即返回并继续执行其他任务。线程需要定期轮询I/O操作的状态，直到操作完成。这种模型适用于I/O操作频繁但每个操作时间较短的场景，例如网络服务器

- java中应用：用java.nio包中的类，如Buffer、Channel和Selector。NIO支持同步非阻塞I/O，允许单个线程处理多个I/O连接。

### 异步IO,AIO

异步I/O是最高效的I/O模型之一。在这种模型中，线程发起一个I/O请求后立即返回，继续执行其他任务。当I/O操作完成时，操作系统会通知线程（通常是通过回调函数或事件通知）。这种模型适用于高并发的场景，如高性能Web服务器

- java中应用：使用java.nio.channels包中的AsynchronousFileChannel和AsynchronousSocketChannel等类。AIO支持真正的异步I/O操作，即操作完成后由系统通知应用程序

## nmap
