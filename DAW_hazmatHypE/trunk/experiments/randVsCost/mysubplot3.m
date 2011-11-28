function [] = mysubplot3(A, B, C, A_label, B_label, C_label, titlename, filename)
figure()

subplot(2,2,1)
plot3(A(:,1),A(:,2),A(:,3), 'x');
hold all; grid on;
plot3(B(:,1),B(:,2),B(:,3), 'o');
plot3(C(:,1),C(:,2),C(:,3), 's');
xlabel('cost')
ylabel('total risk')
zlabel('equity risk')
view(45,30);

subplot(2,2,2)
plot(A(:,1),A(:,2), 'x');
hold all; grid on;
plot(B(:,1),B(:,2), 'o');
plot(C(:,1),C(:,2), 's');
%ylim([4,6]*1e6);
h_legend = legend(A_label, B_label, C_label);
set(h_legend,'FontSize',7);
xlabel('cost')
ylabel('total risk')


subplot(2,2,3)
plot(A(:,1),A(:,3), 'x');
hold all; grid on;
plot(B(:,1),B(:,3), 'o');
plot(C(:,1),C(:,3), 's');
%ylim([2,5]*1e5);
h_legend = legend(A_label, B_label, C_label);
set(h_legend,'FontSize',7);
xlabel('cost')
ylabel('equity risk')


subplot(2,2,4)
plot(A(:,2),A(:,3), 'x');
hold all; grid on;
plot(B(:,2),B(:,3), 'o');
plot(C(:,2),C(:,3), 's');
%ylim([2,5]*1e5);
h_legend = legend(A_label, B_label, C_label);
set(h_legend,'FontSize',7);
xlabel('total risk')
ylabel('equity risk')

% save plot as eps:
exportfig(gcf, filename);

% add title above all for plot on screen:
set(gcf,'NextPlot','add');
axes;
h = title(titlename);
set(gca,'Visible','off');
set(h,'Visible','on'); 


end
